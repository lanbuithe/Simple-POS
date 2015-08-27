package org.pos.domain.logic;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.NamedAttributeNode;
import javax.persistence.NamedEntityGraph;
import javax.persistence.NamedEntityGraphs;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.pos.domain.AbstractAuditingEntity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

/**
 * A OrderNo.
 */
@Entity
@Table(name = "ORDERNO")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@NamedEntityGraphs({
    @NamedEntityGraph(
        name = "orderWithDetails",
        attributeNodes = {
            @NamedAttributeNode("details")
        }
    )
})
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id", scope = OrderNo.class)
public class OrderNo extends AbstractAuditingEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

	@NotNull
    @Column(name = "amount", precision=10, scale=2, nullable = false)
    private BigDecimal amount = new BigDecimal(0);

    @NotNull
    @Size(min = 1, max = 100)
    @Column(name = "status", length = 100, nullable = false)
    private String status;

    @ManyToOne(fetch = FetchType.EAGER)
    private TableNo tableNo;
    
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "orderNo", fetch = FetchType.LAZY)
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private List<OrderDetail> details = new ArrayList<OrderDetail>();
    
    @Column(name = "table_name")
    private String tableName;
    
    @Column(name = "quantity")
    private Integer quantity = 0;
    
    @Column(name = "discount")
    private Integer discount = 0;
    
    @Column(name = "discount_amount", precision=10, scale=2)
    private BigDecimal discountAmount = new BigDecimal(0);
    
    @Column(name = "tax")
    private Integer tax = 0;
    
    @Column(name = "tax_amount", precision=10, scale=2)
    private BigDecimal taxAmount = new BigDecimal(0);
    
    @NotNull
    @Column(name = "receivable_amount")
    private BigDecimal receivableAmount = new BigDecimal(0);

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public TableNo getTableNo() {
        return tableNo;
    }

    public void setTableNo(TableNo tableNo) {
        this.tableNo = tableNo;
    }

    public List<OrderDetail> getDetails() {
		return details;
	}

	public void setDetails(List<OrderDetail> details) {
		this.details = details;
	}
	
	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}	

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}
	
	public Integer getDiscount() {
		return discount;
	}

	public void setDiscount(Integer discount) {
		this.discount = discount;
	}

	public BigDecimal getDiscountAmount() {
		return discountAmount;
	}

	public void setDiscountAmount(BigDecimal discountAmount) {
		this.discountAmount = discountAmount;
	}

	public Integer getTax() {
		return tax;
	}

	public void setTax(Integer tax) {
		this.tax = tax;
	}

	public BigDecimal getTaxAmount() {
		return taxAmount;
	}

	public void setTaxAmount(BigDecimal taxAmount) {
		this.taxAmount = taxAmount;
	}

	public BigDecimal getReceivableAmount() {
		return receivableAmount;
	}

	public void setReceivableAmount(BigDecimal receivableAmount) {
		this.receivableAmount = receivableAmount;
	}

	private void pre() {
		if (null != this.tableNo) {
			this.tableName = this.tableNo.getName();
		}
		if (CollectionUtils.isNotEmpty(this.details)) {
			this.amount = new BigDecimal(0);
			this.quantity = 0;			
			for (OrderDetail orderDetail : this.details) {
				if (null != orderDetail.getAmount()) {
					this.amount = this.amount.add(orderDetail.getAmount());
				}
				if (null != orderDetail.getQuantity()) {
					this.quantity += orderDetail.getQuantity();
				}
			}
		}
		if (null != this.discount && this.discount > 0) {
			this.discountAmount =  this.amount.multiply(new BigDecimal(this.discount));
		}
		if (null != this.tax && this.tax > 0) {
			this.taxAmount =  this.amount.multiply(new BigDecimal(this.tax));
		}
		this.receivableAmount = this.amount.subtract(this.discountAmount).add(this.taxAmount);
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

        OrderNo orderNo = (OrderNo) o;

        if ( ! Objects.equals(id, orderNo.id)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        /*return "OrderNo{" +
                "id=" + id +
                ", amount='" + amount + "'" +
                ", status='" + status + "'" +
                ", table name='" + tableName + "'" +
                ", quantity='" + quantity + "'" +
                '}';*/
    	return ToStringBuilder.reflectionToString(this);
    }

}
