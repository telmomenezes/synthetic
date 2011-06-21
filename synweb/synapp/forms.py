from django import forms


FORMAT_CHOICES = (
    ('synthetic', 'Synthetic'),
    ('snap', 'SNAP'),
)


class AddNetForm(forms.Form):
    name = forms.CharField(label='Name', max_length=200)
    netfile = forms.FileField(label='File')
    fileformat = forms.ChoiceField(label='File Format', choices=FORMAT_CHOICES)
