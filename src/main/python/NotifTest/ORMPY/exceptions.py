class ORMPyException(Exception):
    
    """Exception related to operation with database."""
    def __init__(self, error):
        self.code ,self.message = error
                 
    def __str__(self):
        return self.message

class ORMPyWarning(ORMPyException):

    """Exception raised for important warnings like data truncations
    while inserting, etc."""

class ORMPyError(ORMPyException):

    """Exception that is the base class of all other error exceptions
    (not Warning)."""


class InterfaceError(ORMPyError):

    """Exception raised for errors that are related to the database
    interface rather than the database itself."""


class ORMPyDatabaseError(ORMPyError):

    """Exception raised for errors that are related to the
    database."""


class ORMPyDataError(ORMPyDatabaseError):

    """Exception raised for errors that are due to problems with the
    processed data like division by zero, numeric value out of range,
    etc."""


class ORMPyOperationalError(ORMPyDatabaseError):

    """Exception raised for errors that are related to the database's
    operation and not necessarily under the control of the programmer,
    e.g. an unexpected disconnect occurs, the data source name is not
    found, a transaction could not be processed, a memory allocation
    error occurred during processing, etc."""


class ORMPyIntegrityError(ORMPyDatabaseError):

    """Exception raised when the relational integrity of the database
    is affected, e.g. a foreign key check fails, duplicate key,
    etc."""


class ORMPyInternalError(ORMPyDatabaseError):

    """Exception raised when the database encounters an internal
    error, e.g. the cursor is not valid anymore, the transaction is
    out of sync, etc."""


class ORMPyProgrammingError(ORMPyDatabaseError):

    """Exception raised for programming errors, e.g. table not found
    or already exists, syntax error in the SQL statement, wrong number
    of parameters specified, etc."""


class ORMPyNotSupportedError(ORMPyDatabaseError):

    """Exception raised in case a method or database API was used
    which is not supported by the database, e.g. requesting a
    .rollback() on a connection that does not support transaction or
    has transactions turned off."""