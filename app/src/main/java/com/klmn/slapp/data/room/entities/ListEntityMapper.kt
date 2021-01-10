package com.klmn.slapp.data.room.entities

import com.klmn.slapp.common.EntityModelMapper
import com.klmn.slapp.domain.SlappItem
import com.klmn.slapp.domain.SlappList

object ListEntityMapper : EntityModelMapper<RoomEntities.SList, SlappList> {
    override fun toEntity(model: SlappList) = RoomEntities.SList(
        RoomEntities.ListInfo(
            // fixme this obviously won't work, when working on caching strategy figure out how to manage cached ids
            model.id.toLong(),
            model.name,
            model.user,
            model.timestamp
        ),
        model.items.map {
            RoomEntities.Item(
                model.id.toLong(),
                it.name,
                it.user,
                it.timestamp
            )
        },
        model.users
    )

    override fun toModel(entity: RoomEntities.SList) = SlappList(
        entity.info.id.toString(),
        entity.info.name,
        entity.info.user,
        entity.info.timestamp,
        entity.items.map {
            SlappItem(
                it.name,
                it.user,
                it.timestamp
            )
        }.toMutableList(),
        entity.users
    )
}