from setuptools import setup, find_packages


with open('README.md', 'r') as fh:
    long_description = fh.read()


setup(
    name='synthetic',
    version='0.1',
    author='Telmo Menezes et al.',
    author_email='telmo@telmomenezes.com',
    description='Symbolic generators for complex networks',
    long_description=long_description,
    long_description_content_type='text/markdown',
    url='http://ai4socialscience.github.io/synthetic/',
    classifiers=[
        'Development Status :: 3 - Alpha',
        'Programming Language :: Python :: 3',
        'License :: OSI Approved :: MIT License',
        'Operating System :: OS Independent',
        'Environment :: Console',
        'Intended Audience :: Science/Research',
        'Topic :: Scientific/Engineering :: Artificial Intelligence',
        'Topic :: Scientific/Engineering :: Bio-Informatics',
        'Topic :: Scientific/Engineering :: Information Analysis',
        'Topic :: Scientific/Engineering :: Mathematics',
        'Topic :: Sociology'
    ],
    packages=find_packages(),
    install_requires=[
        'numpy',
        'python-igraph',
        'pyemd',
        'progressbar2',
        'termcolor',
        'jupyter'
    ],
    extras_require={
        'dev': [
            'sphinx',
            'sphinx_rtd_theme'
        ]
    },
    entry_points='''
        [console_scripts]
        synth=synthetic.cli:cli
    '''
)
