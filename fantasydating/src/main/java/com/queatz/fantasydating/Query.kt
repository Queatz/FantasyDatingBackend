package com.queatz.fantasydating

val AQL_UPSERT_PHONE = """
    UPSERT { kind: 'person', token: @token }
        INSERT { kind: 'person', token: @token, created: DATE_ISO8601(DATE_NOW()), updated: DATE_ISO8601(DATE_NOW()) }
        UPDATE { updated: DATE_ISO8601(DATE_NOW()) }
        IN @@entities
        RETURN NEW
"""