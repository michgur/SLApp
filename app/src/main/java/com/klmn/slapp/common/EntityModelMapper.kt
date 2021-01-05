package com.klmn.slapp.common

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

interface EntityModelMapper<E, M> {
    fun toEntity(model: M): E
    fun toModel(entity: E): M

    fun toEntityList(models: Iterable<M>) = models.map(::toEntity)
    fun toModelList(entities: Iterable<E>) = entities.map(::toModel)

    fun toEntityLiveData(model: LiveData<M>) = Transformations.map(model, ::toEntity)
    fun toModelLiveData(entity: LiveData<E>) = Transformations.map(entity, ::toModel)

    fun <C : Iterable<M>> toEntityListLiveData(models: LiveData<C>) =
        Transformations.map(models, ::toEntityList)
    fun <C : Iterable<E>> toModelListLiveData(entities: LiveData<C>) =
        Transformations.map(entities, ::toModelList)

    fun toEntityFlow(model: Flow<M>) = model.map(::toEntity)
    fun toModelFlow(entity: Flow<E>) = entity.map(::toModel)

    fun <C : Iterable<M>> toEntityListFlow(models: Flow<C>) = models.map(::toEntityList)
    fun <C : Iterable<E>> toModelListFlow(entities: Flow<C>) = entities.map(::toModelList)
}