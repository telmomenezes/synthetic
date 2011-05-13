from django.conf.urls.defaults import patterns, include, url

# Uncomment the next two lines to enable the admin:
from django.contrib import admin
admin.autodiscover()

urlpatterns = patterns('',
    url(r'^$', 'synapp.views.networks'),
    url(r'^addnet/$', 'synapp.views.addnet'),
    url(r'^net/(?P<net_id>\d+)/$', 'synapp.views.network'),
    url(r'^genhist/(?P<net_id>\d+)/$', 'synapp.views.genhist'),
    url(r'^hist/(?P<hist_id>\d+)/$', 'synapp.views.histogram'),

    # Examples:
    # url(r'^synweb/', include('synweb.foo.urls')),

    # Uncomment the admin/doc line below to enable admin documentation:
    # url(r'^admin/doc/', include('django.contrib.admindocs.urls')),

    # Uncomment the next line to enable the admin:
    url(r'^admin/', include(admin.site.urls)),
)
