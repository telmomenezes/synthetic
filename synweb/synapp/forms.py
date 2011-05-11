from django import forms


class AddNetForm(forms.Form):
    name = forms.CharField(label='Name', max_length=200)
    net_file = forms.FileField(label='File')
