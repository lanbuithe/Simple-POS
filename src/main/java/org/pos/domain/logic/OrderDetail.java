package org.pos.domain.logic;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.pos.domain.AbstractAuditingEntity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

/**
 * A OrderDetail.
 */
@Entity
@Table(name = "ORDERDETAIL")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id", scope = OrderDetail.class)
public class OrderDetail extends AbstractAuditingEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

	@Min(1)
    @NotNull
    @Column(name = "quantity", nullable = false)
    private Integer quantity = 1;

    @Column(name = "amount", precision=10, scale=2)
    private BigDecimal amount = new BigDecimal(0);

    @ManyToOne(fetch = FetchType.EAGER)
    private OrderNo orderNo;

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER)
    private Item item;
    
    @Column(name = "item_name")
    private String itemName;
    
    @Column(name = "item_category_name")
    private String itemCategoryName;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public OrderNo getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(OrderNo orderNo) {
        this.orderNo = orderNo;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public String getItemName() {
		return itemName;
	}

	public void setItemName(String itemName) {
		this.itemName = itemName;
	}

	public String getItemCategoryName() {
		return itemCategoryName;
	}

	public void setItemCategoryName(String itemCategoryName) {
		this.itemCategoryName = itemCategoryName;
	}
	
	private void pre() {
		if (null != this.item) {
			this.itemName = this.item.getName();
			this.itemCategoryName = this.item.getCategoryName();
			this.amount = this.item.getPrice().multiply(new BigDecimal(this.quantity));
		}
	}
	
	@PrePersist
	public void prePersist() {
		this.pre();
	}
	
	@PreUpdate
	public void preUpdate() {
		this.pre();
	}

	@Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        OrderDetail orderDetail = (OrderDetail) o;

        if ( ! Objects.equals(id, orderDetail.id)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        /*return "OrderDetail{" +
                "id=" + id +
                ", quantity='" + quantity + "'" +
                ", amount='" + amount + "'" +
                ", item name='" + itemName + "'" +
                ", item category name='" + itemCategoryName + "'" +
                '}';*/
    	return ToStringBuilder.reflectionToString(this);
    }
}
