package com.klmn.slapp.data.room.entities

import com.klmn.slapp.common.EntityModelMapper
import com.klmn.slapp.domain.SlappItem
import com.klmn.slapp.domain.SlappList

object ListEntityMapper : EntityModelMapper<Entities.SList, SlappList> {
    override fun toEntity(model: SlappList) = Entities.SList(
        Entities.ListInfo(
            model.id,
            model.name,
            model.user,
            model.timestamp
        ),
        model.items.map {
            Entities.Item(
                model.id,
                it.name,
                it.user,
                it.timestamp
            )
        },
        model.users
    )

    override fun toModel(entity: Entities.SList) = SlappList(
        entity.info.id,
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