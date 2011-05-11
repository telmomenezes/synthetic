from django.http import HttpResponse
from django.template import Context, loader
from synapp.forms import AddNetForm


def networks(request):
    t = loader.get_template('networks.html')
    addnetform = AddNetForm()
    c = Context({
        'addnetform': addnetform,
    })
    return HttpResponse(t.render(c))
