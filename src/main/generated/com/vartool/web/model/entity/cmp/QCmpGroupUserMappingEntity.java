package com.vartool.web.model.entity.cmp;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QCmpGroupUserMappingEntity is a Querydsl query type for CmpGroupUserMappingEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QCmpGroupUserMappingEntity extends EntityPathBase<CmpGroupUserMappingEntity> {

    private static final long serialVersionUID = 823617269L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QCmpGroupUserMappingEntity cmpGroupUserMappingEntity = new QCmpGroupUserMappingEntity("cmpGroupUserMappingEntity");

    public final com.vartool.web.model.entity.base.QAbstractRegAuditorModel _super = new com.vartool.web.model.entity.base.QAbstractRegAuditorModel(this);

    public final QCmpGroupEntity cmpGroupUser;

    public final StringPath groupId = createString("groupId");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> regDt = _super.regDt;

    //inherited
    public final StringPath regId = _super.regId;

    public final com.vartool.web.model.entity.user.QUserEntity userEntity;

    public final StringPath viewid = createString("viewid");

    public QCmpGroupUserMappingEntity(String variable) {
        this(CmpGroupUserMappingEntity.class, forVariable(variable), INITS);
    }

    public QCmpGroupUserMappingEntity(Path<? extends CmpGroupUserMappingEntity> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QCmpGroupUserMappingEntity(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QCmpGroupUserMappingEntity(PathMetadata metadata, PathInits inits) {
        this(CmpGroupUserMappingEntity.class, metadata, inits);
    }

    public QCmpGroupUserMappingEntity(Class<? extends CmpGroupUserMappingEntity> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.cmpGroupUser = inits.isInitialized("cmpGroupUser") ? new QCmpGroupEntity(forProperty("cmpGroupUser")) : null;
        this.userEntity = inits.isInitialized("userEntity") ? new com.vartool.web.model.entity.user.QUserEntity(forProperty("userEntity")) : null;
    }

}

