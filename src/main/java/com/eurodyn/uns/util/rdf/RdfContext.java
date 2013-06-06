/*
 * The contents of this file are subject to the Mozilla Public
 * License Version 1.1 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of
 * the License at http://www.mozilla.org/MPL/
 * 
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 * 
 * The Original Code is Unified Notification System
 * 
 * The Initial Owner of the Original Code is European Environment
 * Agency (EEA).  Portions created by European Dynamics (ED) company are
 * Copyright (C) by European Environment Agency.  All Rights Reserved.
 * 
 * Contributors(s):
 *    Original code: Nedeljko Pavlovic (ED) 
 */

package com.eurodyn.uns.util.rdf;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.Map;

import org.xml.sax.InputSource;

import com.eurodyn.uns.util.URLReader;
import com.eurodyn.uns.util.common.AppConfigurator;
import com.eurodyn.uns.util.common.WDSLogger;
import com.eurodyn.uns.util.xml.XSLTransformer;
import com.hp.hpl.jena.mem.ModelMem;

public class RdfContext {
    private static final WDSLogger logger = WDSLogger.getLogger(RdfContext.class);

    private IChannel channel;
    ModelMem model;


    public RdfContext() {
    }


    public RdfContext(IChannel channel) throws Exception {
        this.channel = channel;
        model = new ModelMem();
        load();
    }


    public Map getData(IRdfProcessStrategy how) throws Exception {
        Map result = null;
        if (channel != null) {
            result = how.collect(model, channel);
        }
        return result;
    }


    private void load() throws Exception {
        load(this.channel);
    }


    private void load(IChannel channel) throws Exception {
        String channelContent = channel.getContent();
        if (channelContent != null)
            model.read(new BufferedReader(new StringReader(channelContent)),"");
        else{
            String URI = channel.getFeedUrl();
            URLReader urlReader=new URLReader();
            ByteArrayInputStream stream = urlReader.getContentAsInStream(URI);
            if (isRss(stream)) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                intoRdf(stream, baos);
                byte result [] = baos.toByteArray();
                model.read(new ByteArrayInputStream(result), "");
                channel.setContent(new String(result,"UTF-8"));
            } else {
                model.read(stream,"");
                channel.setContent( toString(stream));              
            }
        }
    }


    private boolean isRss(ByteArrayInputStream stream) throws Exception {
        boolean result = false;
        BufferedReader in = new BufferedReader(new InputStreamReader(stream));
        char[] part = new char[512];
        stream.mark(5120);
        in.read(part, 0, 512);
        String a = new String(part);
        if (a.toUpperCase().indexOf("RDF:RDF") < 0) {
            result = true;
        }
        stream.reset();
        in.close();
        return result;
    }
    
/*   
    private boolean isRss(byte[] stream) throws Exception {
      return isRss(new ByteArrayInputStream(stream));
    }
*/

    private ByteArrayOutputStream intoRdf(InputStream rssContent, ByteArrayOutputStream baos) throws Exception {
        XSLTransformer transform = new XSLTransformer();
        InputSource source = new InputSource(rssContent);
        //source.setEncoding("UTF-8");
        String xslfilename = AppConfigurator.getInstance().getApplicationHome() + "/xsl/rss2rdf.xsl";
        transform.transform(xslfilename, source, baos, null);
        return baos;
    }
    
    private String toString(final InputStream in) throws IOException {
     
       byte[] buffer = new byte[2048];
       ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
       int bytesRead = in.read(buffer);
       while (bytesRead >= 0) {
           byteOut.write(buffer, 0, bytesRead);
           bytesRead = in.read(buffer);
       }
       return new String(byteOut.toByteArray(),"UTF-8");
    }
    
}
