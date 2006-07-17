
class ValidationException(Exception):
    """Exception raised when a user enters invalid data """
    def __init__(self, message):
        self.message = message
        
    def __str__(self):
        return str(self.message)
        
class NotChOwnerException(Exception):
    """
    Exception raised when a XML-RPC user is not athorized to use PUSH channel
    """
    def __init__(self, message):
        self.message = message
        
    def __str__(self):
        return str(self.message)
