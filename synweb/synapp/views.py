from django.http import HttpResponse
from django.template import Context, loader


def networks(request):
    t = loader.get_template('networks.html')
    c = Context({})
    return HttpResponse(t.render(c))
