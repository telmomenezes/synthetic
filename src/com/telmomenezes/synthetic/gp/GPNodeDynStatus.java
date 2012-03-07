package com.telmomenezes.synthetic.gp;


/**
 * Enum of the possible run-time states of a genetic program node.
 * 
 * Run time states of a GPNode reflect their type of usage during program
 * evaluation, namely: never used, always evaluating to the same value or
 * evaluating to different values.
 * 
 * @author Telmo Menezes (telmo@telmomenezes.com)
 */
public enum GPNodeDynStatus {
	UNUSED, CONSTANT, DYNAMIC 
}
