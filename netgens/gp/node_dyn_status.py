from enum import Enum


class NodeDynStatus(Enum):
	UNUSED = 0
	CONSTANT = 1
	DYNAMIC = 2
