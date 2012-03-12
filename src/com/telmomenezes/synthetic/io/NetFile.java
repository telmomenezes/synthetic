/**
 * 
 */
package com.telmomenezes.synthetic.io;

import com.telmomenezes.synthetic.Net;

/**
 * @author telmo
 *
 */
public abstract class NetFile {
    abstract public Net load(String filePath);
    abstract public void save(Net net, String filePath);
}
