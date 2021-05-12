package com.ordersinkotlin.ordersinkotlin.seedwork

import org.springframework.context.ApplicationContext
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Component
import java.lang.IllegalArgumentException

interface UnitOfWork {
    fun <TEntity : Entity> commit(
        repositoryType: Class<*>,
        entity: TEntity
    )
}

inline fun <reified TRepository : CrudRepository<*, *>> UnitOfWork.commitTo(
    entity: Entity
) {
    this.commit(
        TRepository::class.java,
        entity
    )
}

@Component
class SpringUnitOfWork(
    private val context: ApplicationContext,
    private val eventPublisher: EventPublisher
) : UnitOfWork {
    override fun <TEntity : Entity> commit(
        crudRepositoryType: Class<*>,
        entity: TEntity
    ) {
        val domainEvents = entity.clearAndGetDomainEvents()
        eventPublisher.publishAll(domainEvents)

        when (val repository = context.getBean(crudRepositoryType)) {
            is CrudRepository<*, *> -> (repository as CrudRepository<Entity, *>).save(entity)
            else -> throw IllegalArgumentException("Type $crudRepositoryType is not compatible with type ${CrudRepository::class}")
        }
    }
}