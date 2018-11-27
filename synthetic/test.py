from synthetic.consts import *
from synthetic.generator import generator_from_prog_str


gen = generator_from_prog_str('1', True)
net = gen.run(100, 100, DEFAULT_SAMPLE_RATE)
print(len(net.es))
