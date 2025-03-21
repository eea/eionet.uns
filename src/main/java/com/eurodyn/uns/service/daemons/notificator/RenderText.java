package com.eurodyn.uns.service.daemons.notificator;

import org.python.core.PyObject;
import org.python.core.PyStringMap;
import org.python.util.PythonInterpreter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.StringTokenizer;

public class RenderText {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(RenderText.class);
    
    public static String executeTemplate(String template, PyStringMap namespace, boolean isHtml) {
        try {
            if (isHtml) {
                template = htmlDecode(template);
            }
            
            PythonInterpreter interp = new PythonInterpreter(namespace);
            template = renderValueEval(template, interp);
            template = renderValueExec(template, interp);
            
        } catch(Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return template;
    }
    
    private static String renderValueEval(String code, PythonInterpreter interp) {
        try{
            int start_index = 0;
            while (start_index != -1){
                start_index = code.indexOf("<#");
                int end_index = code.indexOf("#>", start_index); 
                if(start_index != -1 && end_index != -1){
                    String occ = code.substring(start_index + 2, end_index);
                    String rep = code.substring(start_index, end_index + 2);
                    PyObject pyval = interp.eval(occ);
                    String val = pyval.toString();
                    code = code.replace(rep, val);
                }
            }
        } catch(Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return code;
    }
    
    private static String renderValueExec(String code, PythonInterpreter interp) {
        int start_index = 0;
        while (start_index != -1) {
            start_index = code.indexOf("<%");
            int end_index = code.indexOf("%>", start_index); 
            if (start_index != -1 && end_index != -1) {
                String occ = code.substring(start_index + 2, end_index);
                String rep = code.substring(start_index, end_index + 2);
                occ = replaceSeparators(occ);
                occ = alignMultilineCode(occ);
                if (!occ.endsWith("\n")) {
                    occ = occ + "\n";
                }
                StringWriter stringWriter = new StringWriter();
                PrintWriter printWriter = new PrintWriter(stringWriter);
                interp.setOut(printWriter);
                interp.setErr(printWriter);
                interp.exec(occ);
                printWriter.flush();
                String val = stringWriter.toString();
                code = code.replace(rep, val);
                printWriter.close();
            }
        }
        return code;
    }
    
    private static String replaceSeparators(String code) {
        return code.replace("\r\n", "\n").replace("\r", "\n");
    }
    
    private static String alignMultilineCode(String code) {
        
        StringBuffer ret = new StringBuffer();
        
        StringTokenizer lines = new StringTokenizer(code,"\n");
        if(lines.countTokens() == 1){
            code = code.trim();
            return code;
        }
        int indent = findCommonIndentation(lines);
        lines = new StringTokenizer(code, "\n");
        while (lines.hasMoreTokens()) {
            String line = lines.nextToken();
            line = line.substring(indent);
            ret.append(line).append("\n");
        }
        return ret.toString();
    }
    
    private static int findCommonIndentation(StringTokenizer lines) {
        int common_indent = 9999;
        while (lines.hasMoreTokens()) {
            String line = lines.nextToken();
            String trimmed = line.trim();
            if(trimmed.length() > 0) {
                int indent = getIndentation(line);
                if(indent < common_indent)
                    common_indent = indent;
            }
        }
        return common_indent;
    }
    
    private static int getIndentation(String line) {
        int tabsize = 8;
        int indent = 0;
        int len = line.length();
        char c;

        for (int i = 0; i < len; i++) {
            c = line.charAt(i);
            if (c == ' ')
                indent = indent + 1;
            else if (c == '\t')
                indent = indent + tabsize;
            else
                break;
        }
        return indent;
    }
    
    private static String htmlDecode(String code) {
        if (code != null) {
            code.replace("&amp;","&");
            code.replace("&lt;","<");
            code.replace("&gt;",">");
            code.replace("&quot;","\"");
            code.replace("&nbsp;"," ");
        }
        return code;
    }
}
