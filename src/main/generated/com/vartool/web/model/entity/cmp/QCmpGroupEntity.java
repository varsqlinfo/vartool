package com.vartool.web.model.entity.cmp;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QCmpGroupEntity is a Querydsl query type for CmpGroupEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QCmpGroupEntity extends EntityPathBase<CmpGroupEntity> {

    private static final long serialVersionUID = 1354231956L;

    public static final QCmpGroupEntity cmpGroupEntity = new QCmpGroupEntity("cmpGroupEntity");

    public final com.vartool.web.model.entity.base.QAabstractAuditorModel _super = new com.vartool.web.model.entity.base.QAabstractAuditorModel(this);

    public final StringPath description = createString("description");

    public final StringPath groupId = createString("groupId");

    public final SetPath<CmpGroupMappingEntity, QCmpGroupMappingEntity> groupMappingInfos = this.<CmpGroupMappingEntity, QCmpGroupMappingEntity>createSet("groupMappingInfos", CmpGroupMappingEntity.class, QCmpGroupMappingEntity.class, PathInits.DIRECT2);

    public final SetPath<CmpGroupUserMappingEntity, QCmpGroupUserMappingEntity> groupMappingUserInfos = this.<CmpGroupUserMappingEntity, QCmpGroupUserMappingEntity>createSet("groupMappingUserInfos", CmpGroupUserMappingEntity.class, QCmpGroupUserMappingEntity.class, PathInits.DIRECT2);

    public final StringPath layoutInfo = createString("layoutInfo");

    public final StringPath name = createString("name");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> regDt = _super.regDt;

    //inherited
    public final StringPath regId = _super.regId;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updDt = _super.updDt;

    //inherited
    public final StringPath updId = _super.updId;

    public QCmpGroupEntity(String variable) {
        super(CmpGroupEntity.class, forVariable(variable));
    }

    public QCmpGroupEntity(Path<? extends CmpGroupEntity> path) {
        super(path.getType(), path.getMetadata());
    }

    public QCmpGroupEntity(PathMetadata metadata) {
        super(CmpGroupEntity.class, metadata);
    }

}

