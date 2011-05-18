from django.http import HttpResponse, HttpResponseRedirect
from django.template import Context, RequestContext, loader
from django.shortcuts import render_to_response
from synapp.forms import AddNetForm
from synapp.models import Network, DRMap
from synweb.settings import DB_DIR, TMP_DIR
from syn.io import snap2syn
from syn.core import compute_evc, get_evc_histogram, destroy_net, histogram2d_bin_number, histogram2d_get_value


def networks(request):
    addnetform = AddNetForm()
    variables = RequestContext(request, {
        'net_list': Network.objects.all(),
        'addnetform': addnetform,
    })
    return render_to_response('networks.html', variables)


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


def network(request, net_id):
    variables = Context({
        'net': Network.objects.get(id=net_id),
        'map_list': DRMap.objects.filter(net=net_id),
    })
    return render_to_response('network.html', variables)


def gendrmap(request, net_id):

    bins = 25

    map_data = ''

    net = Network.objects.get(id=net_id)
    syn_net = net.getnet()
    compute_evc(syn_net)
    hist = get_evc_histogram(syn_net, bins)


    bin_number = histogram2d_bin_number(hist)
    for x in range(bin_number):
        for y in range(bin_number):
            val = histogram2d_get_value(hist, x, y)
            if (x > 0) or (y > 0):
                map_data += ','
            map_data += '%f' % val

    destroy_net(syn_net)

    map = DRMap(net=net, bins=bins, data=map_data)
    map.save()

    return HttpResponseRedirect('/net/' + net_id)


def drmap(request, map_id):
    variables = Context({
        'map': DRMap.objects.get(id=map_id),
    })
    return render_to_response('drmap.html', variables)
