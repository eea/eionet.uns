import re
import StringIO
import sys
#import traceback
import string

re_code_exec = re.compile("\<%.*?%\>", re.MULTILINE|re.DOTALL)
re_code_eval = re.compile("\<#.*?#\>", re.MULTILINE|re.DOTALL)


htmlCodes = [
    ['&', '&amp;'],
    ['<', '&lt;'],
    ['>', '&gt;'],
    ['"', '&quot;'],
    [' ', '&nbsp;']
]


def htmlDecode(s, codes=htmlCodes):
    """ """
    for code in codes:
        s = string.replace(s, code[1], code[0])
    return s


def executeTestTemplate(template, namespace, isHtml = False):
    try:
        return executeTemplate(template, namespace, isHtml)
    except Exception,err:
        return 'UnsTestException:\n%s' % str(err)

def executeTemplate(template, namespace, isHtml = False):
    if isHtml:
        template = htmlDecode(template)

    occs = re.findall(re_code_eval, template)
    for occ in occs:
        value = render_value_eval(occ, namespace)
        template = template.replace(occ, value)
    occs = re.findall(re_code_exec, template)
    for occ in occs:
        value = render_value_exec(occ, namespace)
        template = template.replace(occ, value)
    return template

def render_value_eval(code, namespace):
    """
    Render code, in the form of a string "<% something %>", to its value by
    evaluating (gasp!) it."""
    code = code[2:-2].strip()
    try:
        value = eval(code, namespace)
    except Exception, e:
        e.namespace = namespace
        raise
    return "%s" % (value,)

def render_value_exec(code, namespace):
    """
    A bit more complex than render_value_eval. We redirect sys.stdout to a
    StringIO, so anything written to (the fake) sys.stdout, or printed, is
    caught by it and placed in HTML instead. Name 'doc' is available as a
    shorthand for sys.stdout.

    Note that multi-line code should start on a separate line:
    <#
       print this
       print that
    #>
    """
    s = StringIO.StringIO()
    oldstdout, sys.stdout = sys.stdout, s
#    code = restyle_code(code[2:-2].strip())
    # remove <# #>
    code = code[2:-2]
    code = replace_separators(code)
    code = align_multiline_code(code)
    if not code.endswith('\n'):
        code += '\n'
    #
    # XXXX could raise SyntaxError
    codeobj = compile(code, '<string>', 'exec')
    #
    namespace["doc"] = sys.stdout
    # for printing tracebacks etc
    namespace['stdout'] = oldstdout
    try:
        try:
            exec codeobj in namespace
        except Exception, e:
#            print >> sys.stderr, "-----An error occurred:-----"
#            traceback.print_exc()
#            print >> sys.stderr, "code:", `code`
#            print >> sys.stderr, "----------------------------"
            e.namespace = namespace
            raise
    finally:
        sys.stdout = oldstdout
    #
    return s.getvalue()

def render_well(template, namespace):
    """ Render until no embedded code is left. """
#    while 1:
    # max 100 iterations
    for i in range(100):
#        print 'x'
        old_template = template
        template = render(template, namespace)
        if (((template.find("<%") == -1) and
                (template.find("<#") == -1)) or
                (template == old_template)):
            # no change? then scram!
            break
    return template

# for some reason, \r\n as line ending is not acceptable for exec...
# so we convert that:
def replace_separators(code):
    """Replace \r\n and \r line separators with \n."""
    return code.replace('\r\n', '\n').replace('\r', '\n')

def align_multiline_code(code):
    """
    Align multi-line code so Python can execute it without running into
    inconsistent indentation.
    """
    lines = code.split("\n")
    #
    # if there's only one line, strip it
    if len(lines) == 1:
        lines[0] =lines[0].strip()
    #
    # dedent as much as possible so at least some lines end up at position 0
    indent = find_common_indentation(lines)
    for i in range(len(lines)):
        lines[i] = lines[i][indent:]
    #
    # glue everything back together and hope for the best ;-)
    return "\n".join(lines)

def find_common_indentation(lines):
    """
    Find the minimum indentation that all lines have.  Ignore empty lines.
    """
    common_indent = 9999
    for line in lines:
        if not line.strip():
            # ignore empty lines
            continue
        indent = get_indentation(line)
        if indent < common_indent:
            common_indent = indent
    return common_indent

def get_indentation(line, tabsize = 8):
    """Get a string's indentation."""
    indent = 0
    for c in line:
        if c == " ":
            indent = indent + 1
        elif c == "\t":
            indent = indent + tabsize
        else:
            break
    return indent


if __name__ == "__main__":
    #
    code = """\
Hi! My name is <% name %>. Pleased to meet ya,
I'm a funky creature.
Let's print some numbers, eh?
<#
for i in range(10):
    print i
#>
I'm a funky creature.
Let's print some numbers, eh?
<% signature %>
I'm a funky creature.
Let's print some numbers, eh?
"""
    #
    name = "Hans"
    signature = "--\nhans@nowak.com"
    #
    print render(code, globals())


"""
CHANGELOG

2005/06/18
Changes by Nicola Larosa
    Code cleanup
        lines shortened
        comments on line above code
        empty comments in empty lines

2005/06/06
Removed extraneous print statement.

2005/05/15
Added 'stdout' to the namespace in ``render_value_exec``
We compile the code to a codeobj in ``render_value_exec``
Replaced use of the string module with string methods.


TODO/ISSUES
Can we improve the error messages ?
    (e.g. the line in the template they occur in ?)

We *could* use ``eval`` in ``render_value_exec``, (for single line statements only)
    and so eliminate the distinction between '<# ... #>' and '<% .... %>'
    (This would prevent us from exec'ing single line fucntion calls though)

Should tabsize be a config option ? (in ``get_indentation``)
"""

