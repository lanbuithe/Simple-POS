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
import javax.persistence.PostPersist;
import javax.persistence.PostUpdate;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.apache.commons.collections4.CollectionUtils;
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
    private Integer quantity = 1;

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
	
	private void post() {
		if (null != this.tableNo) {
			this.tableName = this.tableNo.getName();
		}
		if (CollectionUtils.isNotEmpty(details)) {
			for (OrderDetail orderDetail : details) {
				if (null != orderDetail.getAmount()) {
					this.amount.add(orderDetail.getAmount());
				}
				if (null != orderDetail.getQuantity()) {
					this.quantity += orderDetail.getQuantity();
				}
			}
		}
	}
	
	@PostPersist
	public void postPersist() {
		this.post();
	}
	
	@PostUpdate
	public void postUpdate() {
		this.post();
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
        return "OrderNo{" +
                "id=" + id +
                ", amount='" + amount + "'" +
                ", status='" + status + "'" +
                ", table name='" + tableName + "'" +
                ", quantity='" + quantity + "'" +
                '}';
    }

}
