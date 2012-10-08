package com.evolveum.midpoint.prism.query;

import javax.xml.namespace.QName;

import com.evolveum.midpoint.util.DebugDumpable;
import com.evolveum.midpoint.util.DebugUtil;
import com.evolveum.midpoint.util.Dumpable;

public class ObjectPaging implements Dumpable, DebugDumpable{
	
	private Integer offset;
	private Integer maxSize;
	private QName orderBy;
	private OrderDirection direction;

	
	ObjectPaging() {
		// TODO Auto-generated constructor stub
	}
	
	ObjectPaging(Integer offset, Integer maxSize){
		this.offset = offset;
		this.maxSize = maxSize;
	}
	
	ObjectPaging(Integer offset, Integer maxSize, QName orderBy, OrderDirection direction){
		this.offset = offset;
		this.maxSize = maxSize;
		this.orderBy = orderBy;
		this.direction = direction;
	}
	
	public static ObjectPaging createPaging(Integer offset, Integer maxSize){
		return new ObjectPaging(offset, maxSize);
	}
	
	public static ObjectPaging createPaging(Integer offset, Integer maxSize, QName orderBy, OrderDirection direction){
		return new ObjectPaging(offset, maxSize, orderBy, direction);
	}
	
	public static ObjectPaging createPaging(Integer offset, Integer maxSize, String orderBy, String namespace, OrderDirection direction){
		return new ObjectPaging(offset, maxSize, new QName(namespace, orderBy), direction);
	}
	
	public static ObjectPaging createEmptyPaging(){
		return new ObjectPaging();
	}
	
	public OrderDirection getDirection() {
		return direction;
	}
	public void setDirection(OrderDirection direction) {
		this.direction = direction;
	}
	
	public Integer getOffset() {
		return offset;
	}
	
	public void setOffset(Integer offset) {
		this.offset = offset;
	}
	
	public QName getOrderBy() {
		return orderBy;
	}
	
	public void setOrderBy(QName orderBy) {
		this.orderBy = orderBy;
	}
	
	public Integer getMaxSize() {
		return maxSize;
	}
	
	public void setMaxSize(Integer maxSize) {
		this.maxSize = maxSize;
	}

	@Override
	public String debugDump() {
		return debugDump(0);
	}

	@Override
	public String debugDump(int indent) {
		StringBuilder sb = new StringBuilder();
		sb.append("PAGING: \n");
		DebugUtil.indentDebugDump(sb, indent + 1);
		sb.append("Offset: " + getOffset());
		sb.append("\n");
		sb.append("Max size: " + getMaxSize());
		sb.append("\n");
		sb.append("Order by: " + getOrderBy().toString());
		sb.append("\n");
		sb.append("Order direction: " + getDirection());
		sb.append("\n");	
		return sb.toString();
	}

	@Override
	public String dump() {
		return debugDump(0);
	}
}