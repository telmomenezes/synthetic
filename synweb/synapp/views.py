from django.http import HttpResponse, HttpResponseRedirect
from django.template import Context, RequestContext, loader
from django.shortcuts import render_to_response
from django.contrib.auth.decorators import login_required
from django.contrib.auth import logout
from synapp.forms import AddNetForm
from synapp.models import Network, DRMap
from synweb.settings import DB_DIR, TMP_DIR
from syn.io import snap2syn
from syn.core import *
from shutil import copyfile


@login_required
def networks(request):
    addnetform = AddNetForm()
    variables = RequestContext(request, {
        'net_list': Network.objects.all(),
        'addnetform': addnetform,
    })
    return render_to_response('networks.html', variables)


@login_required
def addnet(request):
    if request.method == 'POST':
        form = AddNetForm(request.POST, request.FILES)
        if form.is_valid():
            name = form.cleaned_data['name']
            fileformat = form.cleaned_data['fileformat']
            net = Network(name=name)
            net.save()

            netfile = request.FILES['netfile']
            tmp_path = TMP_DIR + 'tmpnet'
            tmp_file = open(tmp_path, 'w')
            for chunk in netfile.chunks():
                tmp_file.write(chunk)
            tmp_file.close()

            dest_path = '%s/net_%d' % (DB_DIR, net.id)

            node_count = 0
            edge_count = 0
            temporal = 0
            min_ts = 0
            max_ts = 0

            if fileformat == 'synthetic':
                copyfile(tmp_path, dest_path)
                syn_net = net.getnet()
                node_count = net_node_count(syn_net)
                edge_count = net_edge_count(syn_net)
                temporal = net_temporal(syn_net)
                min_ts = net_min_ts(syn_net)
                max_ts = net_max_ts(syn_net)
                destroy_net(syn_net)

            elif fileformat == 'snap':
                node_count, edge_count = snap2syn(tmp_path, dest_path)
            else:
                # TODO: error message
                return HttpResponseRedirect('/')
        
            net.nodes = node_count
            net.edges = edge_count
            net.temproal = temporal
            net.min_ts = min_ts
            net.max_ts = max_ts
            net.save()

    return HttpResponseRedirect('/')


@login_required
def network(request, net_id):
    variables = RequestContext(request, {
        'net': Network.objects.get(id=net_id),
    })
    return render_to_response('network.html', variables)


@login_required
def gendrmap(request, net_id):
    bins = 50
    steps = 1
    cur_ts = 0
    interval = -1

    map_data = ''

    net = Network.objects.get(id=net_id)

    if net.temporal == 1:
        steps = 10
        interval = (net.max_ts - net.min_ts) / 10
        cur_ts = net.min_ts

    for i in range(steps):
        min_ts = -1
        max_ts = -1
        if interval > 0:
            min_ts = cur_ts
            max_ts = cur_ts + interval
        syn_net = net.getnet(min_ts, max_ts)
        compute_evc(syn_net)
        drmap = get_drmap_with_limits(syn_net, bins, -7.0, 7.0, -7.0, 7.0)
        drmap_log_scale(drmap)
        drmap_normalize(drmap)

        for x in range(bins):
            for y in range(bins):
                val = drmap_get_value(drmap, x, y)
                if (x > 0) or (y > 0):
                    map_data += ','
                map_data += '%f' % val

        destroy_net(syn_net)
        destroy_drmap(drmap)

        cur_ts += interval

    map = DRMap(net=net, bins=bins, steps=1, data=map_data, min_hor=-7.0, max_hor=7.0,
        min_ver=-7.0, max_ver=7.0)
    map.save()

    Network.objects.filter(id=net_id).update(drmap=map.id)

    return HttpResponseRedirect('/net/%s' % net_id)


@login_required
def lab(request):
    map_data = ''
    bins = 50
    type_count = 5
    previous_values = False

    m_links = ''
    m_random = ''
    m_follow = ''
    m_rfollow = ''
    m_weight = ''
    m_stop = ''

    node_count = 0
    edge_count = 0
    max_cycles = 0
    max_walk_length = 0

    if request.method == 'POST':
        previous_values = True
        gen = create_generator(type_count)

        for y in range(type_count):
            for x in range(type_count):
                val = float(request.POST['link_cell_%d_%d' % (x, y)])
                generator_set_link(gen, x, y, val) 
                if m_links != '':
                    m_links += ','
                m_links = '%s%f' % (m_links, val)

                val = float(request.POST['random_cell_%d_%d' % (x, y)])
                generator_set_random(gen, x, y, val) 
                if m_random != '':
                    m_random += ','
                m_random = '%s%f' % (m_random, val)

                val = float(request.POST['follow_cell_%d_%d' % (x, y)])
                generator_set_follow(gen, x, y, val) 
                if m_follow != '':
                    m_follow += ','
                m_follow = '%s%f' % (m_follow, val)

                val = float(request.POST['rfollow_cell_%d_%d' % (x, y)])
                generator_set_rfollow(gen, x, y, val) 
                if m_rfollow != '':
                    m_rfollow += ','
                m_rfollow = '%s%f' % (m_rfollow, val)

        for pos in range(type_count):
            val = float(request.POST['weight_cell_%d' % pos])
            generator_set_weight(gen, pos, val)
            if m_weight != '':
                m_weight += ','
            m_weight = '%s%f' % (m_weight, val)

            val = float(request.POST['stop_cell_%d' % pos])
            generator_set_stop(gen, pos, val)
            if m_stop != '':
                m_stop += ','
            m_stop = '%s%f' % (m_stop, val)

        node_count = int(request.POST['nodes'])
        edge_count = int(request.POST['edges'])
        max_cycles = int(request.POST['cycles'])
        max_walk_length = int(request.POST['max_walk'])

        net = generate_network(gen, node_count, edge_count, max_cycles, max_walk_length)
        compute_evc(net)
        drmap = get_drmap_with_limits(net, bins, -7.0, 7.0, -7.0, 7.0)
        drmap_log_scale(drmap)
        drmap_normalize(drmap)

        for x in range(bins):
            for y in range(bins):
                val = drmap_get_value(drmap, x, y)
                if (x > 0) or (y > 0):
                    map_data += ','
                map_data += '%f' % val

        destroy_net(net)
        destroy_drmap(drmap)
        destroy_generator(gen)

    variables = RequestContext(request, {
        'map_data': map_data,
        'bins': bins,
        'type_count': type_count,
        'previous_values': previous_values,
        'type_list': range(type_count),
        'nodes': node_count,
        'edges': edge_count,
        'cycles': max_cycles,
        'max_walk': max_walk_length,
        'm_links': m_links,
        'm_random': m_random,
        'm_follow': m_follow,
        'm_rfollow': m_rfollow,
        'm_weight': m_weight,
        'm_stop': m_stop,
    })
    return render_to_response('lab.html', variables)


@login_required
def tbd(request):
    variables = RequestContext(request, {
    })
    return render_to_response('tbd.html', variables)


def logout_page(request):
    logout(request)
    return HttpResponseRedirect('/')
