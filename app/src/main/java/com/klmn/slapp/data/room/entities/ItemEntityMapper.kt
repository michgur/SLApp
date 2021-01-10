package com.klmn.slapp.data.room.entities

import com.klmn.slapp.common.EntityModelMapper
import com.klmn.slapp.domain.SlappItem

object ItemEntityMapper : EntityModelMapper<RoomEntities.Item, Pair<Long, SlappItem>> {
    override fun toEntity(model: Pair<Long, SlappItem>) = RoomEntities.Item(
        model.first,
        model.second.name,
        model.second.user,
        model.second.timestamp
    )

    override fun toModel(entity: RoomEntities.Item) = entity.listId to SlappItem(
        entity.name,
        entity.user,
        entity.timestamp
    )
}