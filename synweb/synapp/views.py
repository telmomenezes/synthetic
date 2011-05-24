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
            net = Network(name=name)
            net.save()

            netfile = request.FILES['netfile']
            tmp_path = TMP_DIR + 'tmpnet'
            tmp_file = open(tmp_path, 'w')
            for chunk in netfile.chunks():
                tmp_file.write(chunk)
            tmp_file.close()

            dest_path = '%s/net_%d' % (DB_DIR, net.id)
            node_count, edge_count = snap2syn(tmp_path, dest_path)

            net.nodes = node_count
            net.edges = edge_count
            net.save()

    return HttpResponseRedirect('/')


@login_required
def network(request, net_id):
    variables = RequestContext(request, {
        'net': Network.objects.get(id=net_id),
        'map_list': DRMap.objects.filter(net=net_id),
    })
    return render_to_response('network.html', variables)


@login_required
def gendrmap(request, net_id):
    bins = 50

    map_data = ''

    net = Network.objects.get(id=net_id)
    syn_net = net.getnet()
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

    map = DRMap(net=net, bins=bins, data=map_data, min_hor=-7.0, max_hor=7.0,
        min_ver=-7.0, max_ver=7.0)
    map.save()

    return HttpResponseRedirect('/drmap/%d' % map.id)


@login_required
def drmap(request, map_id):
    variables = RequestContext(request, {
        'map': DRMap.objects.get(id=map_id),
    })
    return render_to_response('drmap.html', variables)


@login_required
def tbd(request):
    variables = RequestContext(request, {
    })
    return render_to_response('tbd.html', variables)


def logout_page(request):
    logout(request)
    return HttpResponseRedirect('/')
