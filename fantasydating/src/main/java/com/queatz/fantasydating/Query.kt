package com.queatz.fantasydating

object AqlParam {
    const val Collection = "@collection"
    const val Graph = "graph"
    const val Token = "token"
    const val Person = "person"
    const val Sex = "sex"
    const val Id = "id"
    const val From = "from"
    const val To = "to"
    const val Min = "min"
    const val Max = "max"
}

object AqlQuery {
    const val UpsertPerson = """
        UPSERT { kind: 'person', token: @token }
            INSERT { kind: 'person', token: @token, created: DATE_ISO8601(DATE_NOW()), updated: DATE_ISO8601(DATE_NOW()) }
            UPDATE { updated: DATE_ISO8601(DATE_NOW()) }
            IN @@collection
            RETURN NEW
    """

    const val DiscoveryPreferencesForPerson = """
        FOR discoveryPreferences IN @@collection
            FILTER discoveryPreferences.kind == 'discovery-preferences'
            FILTER discoveryPreferences.person == @person
            RETURN discoveryPreferences
    """

    const val EventsForPerson = """
        FOR event IN @@collection
            FILTER event.kind == 'event'
            FILTER event.person == @person
            SORT DATE_TIMESTAMP(event.created) DESC
            LIMIT 20
            RETURN event
    """

    const val MessagesBetweenPeople = """
        FOR message IN @@collection
            FILTER message.kind == 'message'
            FILTER (
                (message.from == @person AND message.to == @id)
                OR
                (message.from == @id AND message.to == @person)
            )
            SORT DATE_TIMESTAMP(message.created) DESC
            LIMIT 20
            RETURN message
    """
    const val PersonForPerson = """
        LET person = DOCUMENT(@id)
        
        RETURN MERGE(
            person,
            {
                youLove: LENGTH(
                    FOR personLove, edge IN OUTBOUND @person GRAPH @graph
                        FILTER edge.kind == 'love' AND personLove._id == person._id RETURN true
                ) != 0,
                lovesYou: LENGTH(
                    FOR personLove, edge IN OUTBOUND person GRAPH @graph
                        FILTER edge.kind == 'love' AND personLove._id == @person RETURN true
                ) != 0
            }
        )
    """

    const val PeopleForPerson = """
        LET me = DOCUMENT(@person)
        
        FOR person IN @@collection
            FILTER person.kind == 'person'
                AND person.approved == true
                AND person.active == true
                AND person._id != @person
                AND LENGTH(
                    FOR personHide, edge IN ANY person GRAPH @graph
                        FILTER edge.kind == 'hide' AND personHide._id == @person RETURN true
                ) == 0
                AND LENGTH(
                    FOR discoveryPreferences IN @@collection
                        FILTER discoveryPreferences.kind == 'discovery-preferences'
                            AND me.age >= discoveryPreferences.ageMin
                            AND me.age <= discoveryPreferences.ageMax
                            AND (
                                discoveryPreferences.who == me.sex OR
                                discoveryPreferences.who == 'Person'
                            ) RETURN true
                ) != 0
                AND (
                    person.sex == @sex OR
                    @sex == 'Person'
                )
                AND person.age >= @min
                AND person.age <= @max
                SORT DATE_TIMESTAMP(person.seen) DESC
                LIMIT 20
                RETURN MERGE(
                    person,
                    {
                        youLove: LENGTH(
                            FOR personLove, edge IN OUTBOUND @person GRAPH @graph
                                FILTER edge.kind == 'love' AND personLove._id == person._id RETURN true
                        ) != 0,
                        lovesYou: LENGTH(
                            FOR personLove, edge IN OUTBOUND person GRAPH @graph
                                FILTER edge.kind == 'love' AND personLove._id == @person RETURN true
                        ) != 0
                    }
                )
    """

    const val PeopleWhoLoveEachOther = """
        FOR personYouLove, edgeYouLove IN OUTBOUND @person GRAPH @graph
            FOR personLovesYou, edgeLovesYou IN INBOUND @person GRAPH @graph
                FILTER edgeYouLove.kind == 'love'
                    AND edgeLovesYou.kind == 'love'
                    AND personYouLove == personLovesYou
                RETURN MERGE(
                    personYouLove,
                    {
                        youLove: true,
                        lovesYou: true
                    }
                )
    """

    const val IsPersonHiddenForPerson = """
        RETURN LENGTH(
            FOR personHide, edge IN ANY @id GRAPH @graph
                FILTER edge.kind == 'hide' AND personHide._id == @person RETURN true
        ) != 0
    """

    const val IsBothPeopleLoveEachOther = """
        RETURN LENGTH(
            FOR personLove, edge IN INBOUND @id GRAPH @graph
                FILTER edge.kind == 'love' AND personLove._id == @person RETURN true
        ) != 0 AND LENGTH(
            FOR personLove, edge IN INBOUND @person GRAPH @graph
                FILTER edge.kind == 'love' AND personLove._id == @id RETURN true
        ) != 0
    """

    const val ById = "FOR x IN @@collection FILTER x._key == @id RETURN x"

    const val RemoveLove = """
        FOR love IN @@collection FILTER love.kind == 'love' AND love._from == @from AND love._to == @to
            REMOVE love IN @@collection OPTIONS { ignoreErrors: true }
            RETURN love
    """

    const val AddLove = """UPSERT { kind: 'love', _from: @from, _to: @to }
        INSERT { kind: 'love', _from: @from, _to: @to, created: DATE_ISO8601(DATE_NOW()), updated: DATE_ISO8601(DATE_NOW()) }
        UPDATE { updated: DATE_ISO8601(DATE_NOW()) }
            IN @@collection
            RETURN NEW
    """

    const val HidePerson = """UPSERT { kind: 'hide', _from: @from, _to: @to }
        INSERT { kind: 'hide', _from: @from, _to: @to, created: DATE_ISO8601(DATE_NOW()), updated: DATE_ISO8601(DATE_NOW()) }
        UPDATE { updated: DATE_ISO8601(DATE_NOW()) }
            IN @@collection
            RETURN NEW
    """

    const val Reports = """
        FOR report IN @@collection
            FILTER report.kind == 'report'
                AND report.resolved != true
            SORT DATE_TIMESTAMP(report.created) DESC
            LIMIT 20
            RETURN report
    """

    const val Approvals = """
        FOR person IN @@collection
            FILTER person.kind == 'person'
                AND person.active == true
                AND person.approved == false
            SORT DATE_TIMESTAMP(person.updated) DESC
            LIMIT 20
            RETURN person
    """
}