/*
 * Copyright © 2024 XDEV Software (https://xdev.software)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package software.xdev.spring.data.eclipse.store.integration.shared.tests;

import static software.xdev.spring.data.eclipse.store.integration.shared.repositories.real.life.example.Position.getPositionWithArticleWithName;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import software.xdev.spring.data.eclipse.store.helper.TestUtil;
import software.xdev.spring.data.eclipse.store.integration.shared.DefaultTestAnnotations;
import software.xdev.spring.data.eclipse.store.integration.shared.SharedTestConfiguration;
import software.xdev.spring.data.eclipse.store.integration.shared.repositories.real.life.example.Article;
import software.xdev.spring.data.eclipse.store.integration.shared.repositories.real.life.example.ArticleGroup;
import software.xdev.spring.data.eclipse.store.integration.shared.repositories.real.life.example.Invoice;
import software.xdev.spring.data.eclipse.store.integration.shared.repositories.real.life.example.InvoiceRepository;
import software.xdev.spring.data.eclipse.store.integration.shared.repositories.real.life.example.Position;
import software.xdev.spring.data.eclipse.store.integration.shared.repositories.real.life.example.PositionRepository;
import software.xdev.spring.data.eclipse.store.integration.shared.repositories.real.life.example.Warehouse;


@SuppressWarnings("OptionalGetWithoutIsPresent")
@DefaultTestAnnotations
public class RealLifeTests
{
	public static final String STATIONERY = "Stationery";
	public static final String BUILDING_MATERIAL = "Building Material";
	public static final String PEN = "Pen";
	public static final String BRICK = "Brick";
	public static final int PEN_AMOUNT = 5;
	public static final int BRICK_AMOUNT = 2;
	public static final String WAREHOUSE_WEIDEN = "Weiden";
	@Autowired
	InvoiceRepository invoiceRepository;
	@Autowired
	PositionRepository positionRepository;
	@Autowired
	private SharedTestConfiguration configuration;
	
	private Invoice buildDefaultModel()
	{
		final ArticleGroup stationery = new ArticleGroup(STATIONERY);
		final ArticleGroup buildingMaterial = new ArticleGroup(BUILDING_MATERIAL);
		final Warehouse weidenWarehouse = new Warehouse();
		weidenWarehouse.setName(WAREHOUSE_WEIDEN);
		final Article pen =
			new Article(PEN, stationery, weidenWarehouse);
		final Article brick =
			new Article(BRICK, buildingMaterial, weidenWarehouse);
		final Position position1 = new Position(pen, PEN_AMOUNT);
		final Position position2 = new Position(brick, BRICK_AMOUNT);
		return new Invoice(new ArrayList<>(Arrays.asList(position1, position2)));
	}
	
	private void buildDefaultModelAndSaveIt()
	{
		final Invoice invoice = this.buildDefaultModel();
		this.invoiceRepository.save(invoice);
	}
	
	@Test
	void testReplacePositionWithExistingArticle()
	{
		this.buildDefaultModelAndSaveIt();
		final Invoice invoice = TestUtil.iterableToList(this.invoiceRepository.findAll()).get(0);
		final List<Position> positions = invoice.getPositions();
		final Optional<Position> positionOfBrick = getPositionWithArticleWithName(positions, BRICK);
		final Optional<Position> positionOfPen = getPositionWithArticleWithName(positions, PEN);
		positions.remove(positionOfBrick.get());
		positions.add(new Position(positionOfPen.get().getArticle(), 4));
		this.invoiceRepository.save(invoice);
		
		TestUtil.doBeforeAndAfterRestartOfDatastore(
			this.configuration,
			() -> {
				final Invoice loadedInvoice = TestUtil.iterableToList(this.invoiceRepository.findAll()).get(0);
				Assertions.assertEquals(2, loadedInvoice.getPositions().size());
				final Optional<Position> positionWithAmount4 =
					loadedInvoice.getPositions().stream().filter(position -> position.getAmount() == 4).findFirst();
				Assertions.assertTrue(positionWithAmount4.isPresent());
				Assertions.assertSame(
					loadedInvoice.getPositions().get(0).getArticle(),
					loadedInvoice.getPositions().get(1).getArticle(),
					"Both positions should point to the same article.");
			}
		);
	}
	
	@Test
	void testReplacePositionWithNewArticleThroughInvoiceRepository()
	{
		final String shoeArticleName = "Shoe";
		this.buildDefaultModelAndSaveIt();
		final Invoice invoice = TestUtil.iterableToList(this.invoiceRepository.findAll()).get(0);
		final List<Position> positions = invoice.getPositions();
		positions.remove(1);
		
		final ArticleGroup shoesGroup = new ArticleGroup("Shoes");
		final Article shoe =
			new Article(shoeArticleName, shoesGroup, positions.get(0).getArticle().getWarehouses().get(0));
		positions.add(new Position(shoe, 4));
		this.invoiceRepository.save(invoice);
		
		TestUtil.doBeforeAndAfterRestartOfDatastore(
			this.configuration,
			() -> {
				final Invoice loadedInvoice = TestUtil.iterableToList(this.invoiceRepository.findAll()).get(0);
				final Optional<Position>
					positionOfShoe = getPositionWithArticleWithName(loadedInvoice.getPositions(), shoeArticleName);
				final Optional<Position> positionWithAmount4 =
					loadedInvoice.getPositions().stream().filter(position -> position.getAmount() == 4).findFirst();
				Assertions.assertEquals(2, loadedInvoice.getPositions().size());
				Assertions.assertTrue(positionWithAmount4.isPresent());
				Assertions.assertTrue(positionOfShoe.isPresent());
				Assertions.assertEquals("Shoes", positionOfShoe.get().getArticle().getGroup().getName());
				Assertions.assertSame(
					loadedInvoice.getPositions().get(0).getArticle().getWarehouses().get(0),
					loadedInvoice.getPositions().get(1).getArticle().getWarehouses().get(0));
			}
		);
	}
	
	@Test
	void testAddNewArticleWithSameWarehouse()
	{
		final String shoeArticleName = "Shoe";
		this.buildDefaultModelAndSaveIt();
		final Invoice invoice = TestUtil.iterableToList(this.invoiceRepository.findAll()).get(0);
		final List<Position> positions = invoice.getPositions();
		
		final ArticleGroup shoesGroup = new ArticleGroup("Shoes");
		final Article shoe =
			new Article(shoeArticleName, shoesGroup, positions.get(0).getArticle().getWarehouses().get(0));
		positions.add(new Position(shoe, 4));
		this.invoiceRepository.save(invoice);
		
		TestUtil.doBeforeAndAfterRestartOfDatastore(
			this.configuration,
			() -> {
				final Invoice loadedInvoice = TestUtil.iterableToList(this.invoiceRepository.findAll()).get(0);
				final Optional<Position>
					positionOfShoe = getPositionWithArticleWithName(loadedInvoice.getPositions(), shoeArticleName);
				final Optional<Position> positionWithAmount4 =
					loadedInvoice.getPositions().stream().filter(position -> position.getAmount() == 4).findFirst();
				Assertions.assertEquals(3, loadedInvoice.getPositions().size());
				Assertions.assertTrue(positionWithAmount4.isPresent());
				Assertions.assertTrue(positionOfShoe.isPresent());
				Assertions.assertEquals("Shoes", positionOfShoe.get().getArticle().getGroup().getName());
				Assertions.assertSame(
					loadedInvoice.getPositions().get(0).getArticle().getWarehouses().get(0),
					loadedInvoice.getPositions().get(1).getArticle().getWarehouses().get(0));
				Assertions.assertSame(
					loadedInvoice.getPositions().get(1).getArticle().getWarehouses().get(0),
					loadedInvoice.getPositions().get(2).getArticle().getWarehouses().get(0));
			}
		);
	}
	
	@Test
	void testReplacePositionWithNewArticleThroughPositionRepository()
	{
		final String shoeArticleName = "Shoe";
		this.buildDefaultModelAndSaveIt();
		List<Position> positions = TestUtil.iterableToList(this.positionRepository.findAll());
		this.positionRepository.delete(positions.get(1));
		positions = TestUtil.iterableToList(this.positionRepository.findAll());
		
		final ArticleGroup shoesGroup = new ArticleGroup("Shoes");
		final Article shoe =
			new Article(shoeArticleName, shoesGroup, positions.get(0).getArticle().getWarehouses().get(0));
		positions.add(new Position(shoe, 4));
		this.positionRepository.saveAll(positions);
		
		TestUtil.doBeforeAndAfterRestartOfDatastore(
			this.configuration,
			() -> {
				final List<Position> loadedPositions = TestUtil.iterableToList(this.positionRepository.findAll());
				final Optional<Position>
					positionOfShoe = getPositionWithArticleWithName(loadedPositions, shoeArticleName);
				final Optional<Position> positionWithAmount4 =
					loadedPositions.stream().filter(position -> position.getAmount() == 4).findFirst();
				Assertions.assertEquals(2, loadedPositions.size());
				Assertions.assertTrue(positionWithAmount4.isPresent());
				Assertions.assertTrue(positionOfShoe.isPresent());
				Assertions.assertEquals("Shoes", positionOfShoe.get().getArticle().getGroup().getName());
				Assertions.assertSame(
					loadedPositions.get(0).getArticle().getWarehouses().get(0),
					loadedPositions.get(1).getArticle().getWarehouses().get(0));
			}
		);
	}
	
	@Test
	void testReplacePositionWithNewArticleAndUseImmutableList()
	{
		final String shoeArticleName = "Shoe";
		this.buildDefaultModelAndSaveIt();
		
		final List<Position> positions = TestUtil.iterableToList(this.positionRepository.findAll());
		final ArticleGroup shoesGroup = new ArticleGroup("Shoes");
		final Article shoe =
			new Article(shoeArticleName, shoesGroup, List.of(positions.get(0).getArticle().getWarehouses().get(0)));
		positions.add(new Position(shoe, 4));
		this.positionRepository.saveAll(positions);
		
		TestUtil.doBeforeAndAfterRestartOfDatastore(
			this.configuration,
			() -> {
				final List<Position> loadedPositions = TestUtil.iterableToList(this.positionRepository.findAll());
				Assertions.assertSame(
					loadedPositions.get(0).getArticle().getWarehouses().get(0),
					loadedPositions.get(1).getArticle().getWarehouses().get(0));
			}
		);
	}
	
	@Test
	void testSaveTwoObjectsWithSameReferenceThroughPositionRepository()
	{
		this.buildDefaultModelAndSaveIt();
		TestUtil.doBeforeAndAfterRestartOfDatastore(
			this.configuration,
			() -> {
				final List<Position> positions = TestUtil.iterableToList(this.positionRepository.findAll());
				Assertions.assertSame(
					positions.get(0).getArticle().getWarehouses().get(0),
					positions.get(1).getArticle().getWarehouses().get(0));
			}
		);
	}
	
	@Test
	void testSaveTwoObjectsWithSameReferenceThroughInvoiceRepository()
	{
		this.buildDefaultModelAndSaveIt();
		TestUtil.doBeforeAndAfterRestartOfDatastore(
			this.configuration,
			() -> {
				final List<Invoice> invoices = TestUtil.iterableToList(this.invoiceRepository.findAll());
				Assertions.assertSame(
					invoices.get(0).getPositions().get(0).getArticle().getWarehouses().get(0),
					invoices.get(0).getPositions().get(1).getArticle().getWarehouses().get(0));
			}
		);
	}
	
	@Test
	void testCreateTwoWorkingCopiesThroughTwoRepositories()
	{
		this.buildDefaultModelAndSaveIt();
		TestUtil.doBeforeAndAfterRestartOfDatastore(
			this.configuration,
			() -> {
				final List<Invoice> invoices = TestUtil.iterableToList(this.invoiceRepository.findAll());
				final List<Position> positions = TestUtil.iterableToList(this.positionRepository.findAll());
				Assertions.assertEquals(1, invoices.size());
				Assertions.assertEquals(2, positions.size());
				Assertions.assertNotSame(
					invoices.get(0).getPositions().get(0),
					positions.get(0));
			}
		);
	}
	
	@Test
	void testCreateTwoWorkingCopiesThroughOneRepository()
	{
		this.buildDefaultModelAndSaveIt();
		TestUtil.doBeforeAndAfterRestartOfDatastore(
			this.configuration,
			() -> {
				final List<Invoice> invoices1 = TestUtil.iterableToList(this.invoiceRepository.findAll());
				final List<Invoice> invoices2 = TestUtil.iterableToList(this.invoiceRepository.findAll());
				Assertions.assertNotSame(
					invoices1.get(0),
					invoices2.get(0));
			}
		);
	}
	
	@Test
	void testChangeSharedWarehouse()
	{
		final String newWarehouseName = "Amberg";
		this.buildDefaultModelAndSaveIt();
		final Position position1 = TestUtil.iterableToList(this.positionRepository.findAll()).get(0);
		final Warehouse warehouse = position1.getArticle().getWarehouses().get(0);
		warehouse.setName(newWarehouseName);
		this.positionRepository.save(position1);
		
		TestUtil.doBeforeAndAfterRestartOfDatastore(
			this.configuration,
			() -> {
				final Position position2 = TestUtil.iterableToList(this.positionRepository.findAll()).get(1);
				Assertions.assertEquals(newWarehouseName, position2.getArticle().getWarehouses().get(0).getName());
			}
		);
	}
	
	@Test
	void testSaveSubRepositoryEntities()
	{
		this.buildDefaultModelAndSaveIt();
		final List<Position> positions = TestUtil.iterableToList(this.positionRepository.findAll());
		Assertions.assertEquals(2, positions.size());
		
		final Invoice invoice = TestUtil.iterableToList(this.invoiceRepository.findAll()).get(0);
		invoice.getPositions().clear();
		this.invoiceRepository.save(invoice);
		
		TestUtil.doBeforeAndAfterRestartOfDatastore(
			this.configuration,
			() -> {
				final List<Position> positions2 = TestUtil.iterableToList(this.positionRepository.findAll());
				Assertions.assertEquals(2, positions2.size());
			}
		);
	}
}
