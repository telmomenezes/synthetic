//
//  syntheticAppDelegate.m
//  synthetic
//
//  Created by Telmo Menezes on 3/31/11.
//  Copyright 2011 Telmo Menezes. All rights reserved.
//

#import "syntheticAppDelegate.h"
#import "Network.h"

@implementation syntheticAppDelegate

@synthesize window;

- (void)applicationDidFinishLaunching:(NSNotification *)aNotification
{
    // Insert code here to initialize your application
    Network* net = new Network();
    delete net;
}

@end
