# Synthetic
**Symbolic Generators for Complex Networks**

_**NOTE:** Synthetic was originally written in Java, and this is an ongoing effort to port it to Python. This Python version is not yet functional. The original and fully-functional Java version of Synthetic can be found here: https://github.com/telmomenezes/synthetic/ ._

Synthetic is a machine learning tool that can be used to discover plausible generators for complex networks. Generators are simple computer programs that control the growth of a network from the bottom-up, in a similar fashion to the processes believed to underlie the emergence of many different types of networks, be them biological, social or technological.

Generators are useful as network growth models, both for their potentially explanatory and predictive powers. In a way, this tool automates the scientific method. It creates and refines hypothesis, and tests them against real data, in a process that leads to increasingly plausible models.

Programs are represented in a very simple language that is suitable both for humans and the machine learning process. The machine learning algorithm used is Genetic Programming, belonging to the family of Evolutionary Algorithms, which are inspired by Darwinian evolution. Programs are subject to random variations, much like the genetic material in biological entities. The programs with the highest quality survive, leading to an ongoing refinement of the growth model.

For a more complete explanation see this article: http://www.nature.com/srep/2014/140905/srep06284/full/srep06284.html

## Related publications

Menezes, T. and Roth, C., Symbolic regression of generative network models, Scientific Reports 4 (2014) http://www.nature.com/srep/2014/140905/srep06284/full/srep06284.html
