package ata.unit.three.project.expense.dynamodb;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.model.AttributeDefinition;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.CreateTableResult;
import com.amazonaws.services.dynamodbv2.model.GlobalSecondaryIndex;
import com.amazonaws.services.dynamodbv2.model.KeySchemaElement;
import com.amazonaws.services.dynamodbv2.model.KeyType;
import com.amazonaws.services.dynamodbv2.model.ListTablesResult;
import com.amazonaws.services.dynamodbv2.model.Projection;
import com.amazonaws.services.dynamodbv2.model.ProjectionType;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.amazonaws.services.dynamodbv2.model.ScalarAttributeType;
import com.amazonaws.services.dynamodbv2.util.TableUtils;
import com.kenzie.ata.ExcludeFromJacocoGeneratedReport;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

@ExcludeFromJacocoGeneratedReport
public class ExpenseTable {
    public static String EXPENSE_TABLE_NAME = "Expense";
    public static String EXPENSE_LIST_TABLE_NAME = "ExpenseList";
    static final Logger log = LogManager.getLogger();
    static final AmazonDynamoDB ddb = AmazonDynamoDBClientBuilder.defaultClient();

    @ExcludeFromJacocoGeneratedReport
    public static void createExpenseTable() {
        final String USAGE = "\n" +
                "Usage:\n" +
                "    CreateTable <table>\n\n" +
                "Where:\n" +
                "    table - the table to create.\n\n" +
                "Example:\n" +
                "    CreateTable Expense table\n";


        log.info(
                "Creating table \"%s\" with a simple primary key: \"email\".\n",
                EXPENSE_TABLE_NAME);

        GlobalSecondaryIndex emailIndex = new GlobalSecondaryIndex()
                .withIndexName("EmailIndex")
                .withProvisionedThroughput(new ProvisionedThroughput()
                        .withReadCapacityUnits((long) 10)
                        .withWriteCapacityUnits((long) 1))
                .withProjection(new Projection().withProjectionType(ProjectionType.ALL));


        ArrayList<KeySchemaElement> indexKeySchema = new ArrayList<>();
        indexKeySchema.add(new KeySchemaElement()
                .withAttributeName("Email")
                .withKeyType(KeyType.HASH));

        emailIndex.setKeySchema(indexKeySchema);

        CreateTableRequest request = new CreateTableRequest()
                .withAttributeDefinitions(
                        new AttributeDefinition("Id", ScalarAttributeType.S),
                        new AttributeDefinition("Email", ScalarAttributeType.S)
                )
                .withKeySchema(
                        new KeySchemaElement("Id", KeyType.HASH)
                )
                .withProvisionedThroughput(new ProvisionedThroughput(
                        10L, 10L))
                .withGlobalSecondaryIndexes(emailIndex)
                .withTableName(EXPENSE_TABLE_NAME);

        try {
            CreateTableResult result = ddb.createTable(request);
            log.info(result.getTableDescription().getTableName());
            TableUtils.waitUntilActive(ddb, EXPENSE_TABLE_NAME);
        } catch (AmazonServiceException | InterruptedException e) {
            log.error(e.getMessage());
        }
    }

    public static void createExpenseListTable() {
        final String USAGE = "\n" +
                "Usage:\n" +
                "    CreateTable <table>\n\n" +
                "Where:\n" +
                "    table - the table to create.\n\n" +
                "Example:\n" +
                "    CreateTable Expense List Table\n";


        log.info(
                "Creating table \"%s\" with a simple primary key: \"email\".\n",
                EXPENSE_LIST_TABLE_NAME);

        GlobalSecondaryIndex emailIndex = new GlobalSecondaryIndex()
                .withIndexName("EmailIndex")
                .withProvisionedThroughput(new ProvisionedThroughput()
                        .withReadCapacityUnits((long) 10)
                        .withWriteCapacityUnits((long) 1))
                .withProjection(new Projection().withProjectionType(ProjectionType.ALL));


        ArrayList<KeySchemaElement> indexKeySchema = new ArrayList<>();
        indexKeySchema.add(new KeySchemaElement()
                .withAttributeName("Email")
                .withKeyType(KeyType.HASH));


        emailIndex.setKeySchema(indexKeySchema);

        CreateTableRequest request = new CreateTableRequest()
                .withAttributeDefinitions(
                        new AttributeDefinition("Id", ScalarAttributeType.S),
                        new AttributeDefinition("Email", ScalarAttributeType.S)
                )
                .withKeySchema(
                        new KeySchemaElement("Id", KeyType.HASH)
                )
                .withProvisionedThroughput(new ProvisionedThroughput(
                        10L, 10L))
                .withGlobalSecondaryIndexes(emailIndex)
                .withTableName(EXPENSE_LIST_TABLE_NAME);

        try {
            CreateTableResult result = ddb.createTable(request);
            log.info(result.getTableDescription().getTableName());
            TableUtils.waitUntilActive(ddb, EXPENSE_LIST_TABLE_NAME);
        } catch (AmazonServiceException | InterruptedException e) {
            log.error(e.getMessage());
        }
    }

    public static boolean doesExpenseTableExist(String table) {
        ListTablesResult tables = ddb.listTables();
        List<String> tableNames = tables.getTableNames();

        for (String tableName : tableNames) {
            if (tableName.equalsIgnoreCase(table)) {
                return true;
            }
        }
        return false;
    }
}
