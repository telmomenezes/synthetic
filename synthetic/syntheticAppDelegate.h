//
//  syntheticAppDelegate.h
//  synthetic
//
//  Created by Telmo Menezes on 3/31/11.
//  Copyright 2011 Telmo Menezes. All rights reserved.
//

#import <Cocoa/Cocoa.h>

@interface syntheticAppDelegate : NSObject <NSApplicationDelegate> {
@private
    NSWindow *window;
}

@property (assign) IBOutlet NSWindow *window;

@end
