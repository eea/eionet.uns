import os

if (os.environ.get("ZOPE_HOME")):
    import UNS
    from ImageFile import ImageFile
    
    
    def initialize(context):
        """ initialize the UNS product """
        context.registerClass(
                UNS.UNS,
                permission = 'Add UNS instance',
                constructors = (
                        UNS.manage_add_uns_form,
                        UNS.manage_add_uns
                ),
                icon = 'www/images/favicon.ico',
            )
        
    misc_ = {}
    
    for root, dirs, files in os.walk(os.path.join(os.path.dirname(__file__), "www/images")):
        for file in files:
            if file[-3:] == "gif" or  file[-3:] == "jpg" or file[-3:] == "png" or file[-3:] == "ico":
                misc_[file] = ImageFile('www/images/%s' % file, globals())    
