package com.klmn.slapp.data.room.entities

import com.klmn.slapp.common.EntityModelMapper
import com.klmn.slapp.domain.SlappItem
import com.klmn.slapp.domain.SlappList

object ListEntityMapper : EntityModelMapper<RoomEntities.SList, Pair<SlappList, EntityState>> {
    override fun toEntity(model: Pair<SlappList, EntityState>) = RoomEntities.SList(
        RoomEntities.ListInfo(
            0L,
            model.first.id,
            model.first.name,
            model.first.user,
            model.first.timestamp,
            model.second.ordinal
        ),
        model.first.items.map {
            RoomEntities.Item(
                model.first.id.toLong(),
                it.name,
                it.user,
                it.timestamp,
                model.second.ordinal
            )
        },
        model.first.users
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
    ) to EntityState.values()[entity.info.state]
}