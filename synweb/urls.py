from django.conf.urls.defaults import patterns, include, url

# Uncomment the next two lines to enable the admin:
from django.contrib import admin
admin.autodiscover()

urlpatterns = patterns('',
    url(r'^$', 'synapp.views.networks'),
    url(r'^tbd/$', 'synapp.views.tbd'),
    url(r'^addnet/$', 'synapp.views.addnet'),
    url(r'^net/(?P<net_id>\d+)/$', 'synapp.views.network'),
    url(r'^gendrmap/(?P<net_id>\d+)/$', 'synapp.views.gendrmap'),
    url(r'^lab/$', 'synapp.views.lab'),

    url(r'^accounts/login/$', 'django.contrib.auth.views.login'),
    url(r'^logout/$', 'synapp.views.logout_page'),

    url(r'^admin/doc/', include('django.contrib.admindocs.urls')),
    url(r'^admin/', include(admin.site.urls)),
)
