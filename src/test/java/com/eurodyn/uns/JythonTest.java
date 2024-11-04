package com.eurodyn.uns;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

import org.python.core.*;
import org.python.util.PythonInterpreter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Tests for the Python templates.
 */
public class JythonTest {

    private Logger LOGGER = LoggerFactory.getLogger(JythonTest.class);

    /**
     * Test 2 + 2 is 4
     */
    @Test
    public void execInterpreter() {
        PythonInterpreter interp =  new PythonInterpreter(null, new PySystemState());;
        PySystemState sys = Py.getSystemState();
        interp.exec("x = 2+2");
        PyObject x = interp.get("x");
        assertEquals("4", x.toString());
    }

   
    @Test
    public void importRe() {
        PythonInterpreter interp =  new PythonInterpreter(null, new PySystemState());;
        PySystemState sys = Py.getSystemState();
        interp.exec("import re");
    }

    @Test
    public void testUnicode() {
        PythonInterpreter interpreter = new PythonInterpreter();

        interpreter.set("mystr", new PyString("Søren Roug")); // Test with char from Latin-1
        PyObject myStr = interpreter.get("mystr");
        LOGGER.info(String.valueOf(myStr));

        interpreter.set("mystr", new PyUnicode("€ as Java String"));
        myStr = interpreter.get("mystr");
        LOGGER.info(String.valueOf(myStr));

        interpreter.exec("mystr = u'€ as Python UNICODE'");
        myStr = interpreter.get("mystr");
        LOGGER.info(String.valueOf(myStr));
    }

}
