from setuptools import setup, find_packages


setup(
    name='netgens',
    version='0.1',
    packages=find_packages(),
    install_requires=[
        'numpy',
        'python-igraph',
        'pyemd',
        'jupyter',
        'progressbar2'
    ],
    entry_points='''
        [console_scripts]
        netgens=netgens.cli:cli
    '''
)
