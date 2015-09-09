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
import javax.validation.constraints.Size;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.pos.domain.AbstractAuditingEntity;

/**
 * A Item.
 */
@Entity
@Table(name = "ITEM")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Item extends AbstractAuditingEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Size(min = 1, max = 100)
    @Column(name = "name", length = 100, nullable = false)
    private String name;
    
    @Min(1)
    @NotNull
    @Column(name = "price", precision=10, scale=2, nullable = false)
    private BigDecimal price;    

    @Size(max = 255)
    @Column(name = "description", length = 255)
    private String description;

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER)
    private ItemCategory category;
    
    @Column(name = "category_name")
    private String categoryName;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ItemCategory getCategory() {
        return category;
    }

    public void setCategory(ItemCategory itemCategory) {
        this.category = itemCategory;
    }

    public String getCategoryName() {
		return categoryName;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}
	
	private void pre() {
		if (null != this.category) {
			this.categoryName = this.category.getName();
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

        Item item = (Item) o;

        if ( ! Objects.equals(id, item.id)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        /*return "Item{" +
                "id=" + id +
                ", name='" + name + "'" +
                ", description='" + description + "'" +
                ", category name='" + categoryName + "'" +
                '}';*/
    	return ToStringBuilder.reflectionToString(this);
    }
}
