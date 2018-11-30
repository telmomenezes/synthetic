from setuptools import setup, find_packages


setup(
    name='synthetic',
    version='0.1',
    packages=find_packages(),
    install_requires=[
        'numpy',
        'python-igraph',
        'pyemd',
        'jupyter',
        'progressbar2',
        'termcolor'
    ],
    entry_points='''
        [console_scripts]
        synth=synthetic.cli:cli
    '''
)
