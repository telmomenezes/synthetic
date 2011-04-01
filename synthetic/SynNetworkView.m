//
//  SynNetworkView.m
//  synthetic
//
//  Created by Telmo Menezes on 4/1/11.
//  Copyright 2011 Telmo Menezes. All rights reserved.
//

#import "SynNetworkView.h"


@implementation SynNetworkView

- (id)initWithFrame:(NSRect)frame
{
    self = [super initWithFrame:frame];
    if (self) {
        // Initialization code here.
    }
    
    return self;
}

- (void)dealloc
{
    [super dealloc];
}

- (void)drawRect:(NSRect)dirtyRect
{
    // Drawing code here.
    CGContext* myContext = (CGContext*)[[NSGraphicsContext currentContext] graphicsPort];
    CGContextSetRGBFillColor (myContext, 1, 0, 0, 1);
    CGContextFillRect (myContext, CGRectMake (0, 0, 200, 100 ));
    CGContextSetRGBFillColor (myContext, 0, 0, 1, .5);
    CGContextFillRect (myContext, CGRectMake (0, 0, 100, 200));
}

@end
