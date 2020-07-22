package com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.utils;

import static org.junit.jupiter.api.Assertions.fail;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.codehaus.groovy.syntax.Types;

import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.model.ItemEntity;
import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.model.OrderEntity;

import io.agroal.api.AgroalDataSource;

@Singleton
public class TestJdbcUtil {

	@Inject
	AgroalDataSource dataSource;

	public ItemEntity getItemById(Integer id) {
		try {
			try (Connection connection = dataSource.getConnection()) {
				try (PreparedStatement statement = connection.prepareStatement(
						"SELECT `id`,`value`,`uniqueValue`,`status` FROM `ItemEntity` WHERE `id` = ?")) {
					statement.setInt(1, id);
					try (ResultSet resultSet = statement.executeQuery()) {
						while (resultSet.next()) {
							return new ItemEntity(resultSet.getInt("id"), resultSet.getInt("value"),
									resultSet.getInt("uniqueValue"), resultSet.getInt("status"));
						}
					}
				}
			}
		} catch (Exception e) {
			fail("Invalid Test JDBC Configuration");
		}
		return null;
	}

	public boolean existsItemById(Integer id) {
		try {
			try (Connection connection = dataSource.getConnection()) {
				try (PreparedStatement statement = connection
						.prepareStatement("SELECT `id` FROM `ItemEntity` WHERE `id` = ?")) {
					statement.setInt(1, id);
					try (ResultSet resultSet = statement.executeQuery()) {
						while (resultSet.next()) {
							return true;
						}
					}
				}
			}
		} catch (Exception e) {
			fail("Invalid Test JDBC Configuration");
		}
		return false;
	}

	public List<ItemEntity> getAllItems() {
		List<ItemEntity> list = new ArrayList<>();
		try {
			try (Connection connection = dataSource.getConnection()) {
				try (PreparedStatement statement = connection
						.prepareStatement("SELECT `id`,`value`,`uniqueValue`,`status` FROM `ItemEntity`")) {

					try (ResultSet resultSet = statement.executeQuery()) {
						while (resultSet.next()) {
							Object unique = resultSet.getObject("uniqueValue");
							list.add(new ItemEntity(resultSet.getInt("id"), resultSet.getInt("value"),
									(unique != null ? (Integer) unique : null), resultSet.getInt("status")));
						}
					}
				}
			}
		} catch (Exception e) {
			fail("Invalid Test JDBC Configuration");
		}
		return list;
	}

	public long countAllItems() {
		try {
			try (Connection connection = dataSource.getConnection()) {
				try (PreparedStatement statement = connection
						.prepareStatement("SELECT count(`id`) FROM `ItemEntity`")) {
					try (ResultSet resultSet = statement.executeQuery()) {
						while (resultSet.next()) {
							return resultSet.getLong(1);
						}
					}
				}
			}
		} catch (Exception e) {
			fail("Invalid Test JDBC Configuration");
		}
		return -1;
	}

	public ItemEntity putItem(ItemEntity entity) {
		try {
			Integer idLazy = null, idChild = null;
			try (Connection connection = dataSource.getConnection()) {
				if (entity.getLazy() != null) {
					try (PreparedStatement statement = connection.prepareStatement(
							"INSERT INTO `LazyEntity` (`id`,`value`) VALUES (?,?)")) {
						statement.setInt(1, entity.getLazy().getId());
						statement.setInt(2, entity.getLazy().getValue());
						idLazy = statement.executeUpdate();
					}
				}
				if (entity.getChild() != null) {
					try (PreparedStatement statement = connection.prepareStatement(
							"INSERT INTO `ChildEntity` (`id`,`value`) VALUES (?,?)")) {
						statement.setInt(1, entity.getChild().getId());
						statement.setInt(2, entity.getChild().getValue());
						idChild = statement.executeUpdate();
					}
				}
				try (PreparedStatement statement = connection.prepareStatement(
						"INSERT INTO `ItemEntity` (`id`,`value`,`uniqueValue`,`status`, `lazy_id`, `child_id`) VALUES (?,?,?,?,?,?)")) {
					statement.setInt(1, entity.getId());
					statement.setInt(2, entity.getValue());
					if (entity.getUniqueValue() != null) {
						statement.setInt(3, entity.getUniqueValue());
					} else {
						statement.setNull(3, Types.NUMBER);
					}
					statement.setInt(4, entity.getStatus());
					if (idLazy == null) {
						statement.setNull(5, Types.NUMBER);
					} else {
						statement.setInt(5, entity.getLazy().getId());
					}
					if (idChild == null) {
						statement.setNull(6, Types.NUMBER);
					} else {
						statement.setInt(6, entity.getChild().getId());
					}
					statement.executeUpdate();
				}
			}
		} catch (Exception e) {
			fail("Invalid Test JDBC Configuration");
		}
		return entity;
	}
	
	public List<ItemEntity> putAllItems(List<ItemEntity> entities) {
		for (ItemEntity entity : entities) {
			putItem(entity);
		}
		return entities;
	}
	
	public OrderEntity putOrder(OrderEntity entity) {
		try {
			try (Connection connection = dataSource.getConnection()) {
				try (PreparedStatement statement = connection.prepareStatement(
						"INSERT INTO `OrderEntity` (`id`,`number`,`productId`,`user`, `total`) VALUES (?,?,?,?,?)")) {
					statement.setInt(1, entity.getId());
					if (entity.getNumber() != null) {
						statement.setInt(2, entity.getNumber());						
					} else {
						statement.setNull(2, Types.NUMBER);
					}
					if (entity.getProductId() != null) {
						statement.setString(3, entity.getProductId());
					} else {
						statement.setNull(3, Types.STRING);
					}
					if (entity.getUser() != null) {
						statement.setString(4, entity.getUser());
					} else {
						statement.setNull(4, Types.STRING);
					}
					if (entity.getTotal() != null) {
						statement.setDouble(5, entity.getTotal());
					} else {
						statement.setNull(5, Types.NUMBER);
					}
					statement.executeUpdate();
				}
			}
		} catch (Exception e) {
			fail("Invalid Test JDBC Configuration");
		}
		return entity;
	}

}
