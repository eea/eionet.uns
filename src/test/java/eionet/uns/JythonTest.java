package eionet.uns;

import org.junit.Ignore;
import org.junit.Test;
import static org.junit.Assert.assertEquals;

import org.python.core.Py;
import org.python.core.PyObject;
import org.python.core.PyInteger;
import org.python.core.PyException;
import org.python.core.PyString;
import org.python.core.PySystemState;
import org.python.util.PythonInterpreter;

/**
 * Tests for the Python templates.
 */
public class JythonTest {

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

}
