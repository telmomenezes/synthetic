//
//  SynNetworkView.m
//  synthetic
//
//  Created by Telmo Menezes on 4/1/11.
//  Copyright 2011 Telmo Menezes. All rights reserved.
//

#import "SynNetworkView.h"
#import "utils.h"


@implementation SynNetworkView

- (id)initWithFrame:(NSRect)frame
{
    self = [super initWithFrame:frame];
    if (self) {
        // Initialization code here.
        net = new Network();
        net->load("/Users/telmo/projects/synthetic/nets/wiki-Vote.csv");
        NSLog(@"node count: %d", net->getNodeCount());
        NSLog(@"edge count: %d", net->getEdgeCount());
        
        vector<Node*>& nodes = net->getNodes();
        
        // temporary code to init node positions
        for (vector<Node*>::iterator iterNode = nodes.begin();
             iterNode != nodes.end();
             iterNode++)
        {
            (*iterNode)->x = RANDOM_UNIFORM * 1000;
            (*iterNode)->y = RANDOM_UNIFORM * 1000;
        }
    }
    
    return self;
}

- (void)dealloc
{
    delete net;
    [super dealloc];
}

- (void)drawNode:(Node*)node toContext:(CGContext*)context
{
    double r = 5;
    double hr = r / 2.0;
    CGContextSetRGBFillColor(context, 0, 0.3, 0.7, 1);
    CGContextFillEllipseInRect(context, CGRectMake(node->x - hr, node->y - hr, r, r));
}

- (void)drawNodeEdges:(Node*)node toContext:(CGContext*)context
{
    vector<Node*>* targets = node->getTargets();
    
    for (vector<Node*>::iterator iterNode = targets->begin();
         iterNode != targets->end();
         iterNode++)
    {
        Node* target = (*iterNode);
                
        CGContextMoveToPoint(context, node->x, node->y);
        CGContextAddLineToPoint(context, target->x, target->y);
    }
}

- (void)drawRect:(NSRect)dirtyRect
{
    CGContext* context = (CGContext*)[[NSGraphicsContext currentContext] graphicsPort];
    
    vector<Node*>& nodes = net->getNodes();
    
    CGContextSetRGBStrokeColor(context, 0.3, 0.3, 0.3, 1.0);
    CGContextSetLineWidth(context, 0.2);
    CGContextBeginPath(context);
    for (vector<Node*>::iterator iterNode = nodes.begin();
         iterNode != nodes.end();
         iterNode++)
    {
        [self drawNodeEdges:(*iterNode) toContext:context];
    }
    CGContextStrokePath(context);
    
    for (vector<Node*>::iterator iterNode = nodes.begin();
         iterNode != nodes.end();
         iterNode++)
    {
        
        [self drawNode:(*iterNode) toContext:context];
    }
}

@end
