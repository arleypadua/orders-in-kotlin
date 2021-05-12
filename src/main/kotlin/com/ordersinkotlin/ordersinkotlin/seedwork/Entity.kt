package com.ordersinkotlin.ordersinkotlin.seedwork

import org.springframework.data.annotation.Transient

abstract class Entity {

    @Transient
    private val domainEvents = hashSetOf<DomainEvent>()

    protected fun raise(domainEvent: DomainEvent) {
        domainEvents.add(domainEvent)
    }

    internal fun clearAndGetDomainEvents(): List<DomainEvent> {
        val domainEvents = domainEvents.toList()
        this.domainEvents.clear()
        return domainEvents
    }

    internal fun peekDomainEvents() = domainEvents.toList()
}

interface DomainEvent