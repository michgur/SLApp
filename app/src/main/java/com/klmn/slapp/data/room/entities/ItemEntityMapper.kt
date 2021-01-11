package com.klmn.slapp.data.room.entities

import com.klmn.slapp.common.EntityModelMapper
import com.klmn.slapp.domain.SlappItem

object ItemEntityMapper : EntityModelMapper<RoomEntities.Item, Triple<Long, SlappItem, EntityState>> {
    override fun toEntity(model: Triple<Long, SlappItem, EntityState>) = RoomEntities.Item(
        model.first,
        model.second.name,
        model.second.user,
        model.second.timestamp,
        model.third.ordinal
    )

    override fun toModel(entity: RoomEntities.Item) = Triple(
        entity.listId,
        SlappItem(
            entity.name,
            entity.user,
            entity.timestamp
        ),
        EntityState.values()[entity.state]
    )
}