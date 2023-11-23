package com.hiraeth.im.business.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.Version;
import com.hiraeth.im.common.util.DateUtil;
import org.apache.ibatis.type.JdbcType;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author: linxueqi
 * @description:
 * @date: 2023/11/23 15:10
 */
public class BaseModel implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 创建时间
	 */
	@JSONField(format = DateUtil.DATETIME_FORMAT)
	@TableField(value = "init_time", jdbcType = JdbcType.TIMESTAMP)
	private Date initTime;

	/**
	 * 创建人
	 */
	@TableField(value = "init_by", jdbcType = JdbcType.VARCHAR, fill = FieldFill.INSERT)
	private Integer initBy;

	/**
	 * 更新时间
	 */
	@JSONField(format = DateUtil.DATETIME_FORMAT)
	@TableField(value = "last_upd_time", jdbcType = JdbcType.TIMESTAMP, fill = FieldFill.UPDATE)
	private Date lastUpdTime;

	/**
	 * 更新人
	 */
	@TableField(value = "last_upd_by", jdbcType = JdbcType.VARCHAR, fill = FieldFill.UPDATE)
	private Integer lastUpdBy;

	/**
	 * 数据版本号,乐观锁控制
	 */
	@Version
	@TableField(value = "version", jdbcType = JdbcType.INTEGER)
	private Integer version;

	@TableField(value = "deleted", jdbcType = JdbcType.BOOLEAN)
	private boolean deleted;

	public boolean getDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	public Date getInitTime() {
		return initTime;
	}

	public void setInitTime(Date initTime) {
		this.initTime = initTime;
	}

	public Integer getInitBy() {
		return initBy;
	}

	public void setInitBy(Integer initBy) {
		this.initBy = initBy;
	}

	public Date getLastUpdTime() {
		return lastUpdTime;
	}

	public void setLastUpdTime(Date lastUpdTime) {
		this.lastUpdTime = lastUpdTime;
	}

	public Integer getLastUpdBy() {
		return lastUpdBy;
	}

	public void setLastUpdBy(Integer lastUpdBy) {
		this.lastUpdBy = lastUpdBy;
	}

	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

}
