import pymysql
import csv

db = pymysql.connect("localhost", "root", "akash", "XYPAY")

cursor = db.cursor()

cursor.execute('SELECT id FROM payer WHERE name = %s', "ISMAIL FOREIGN EXCHANGE BUREAU")

payer_id = cursor.fetchone()

with open('cash-pickup-location.csv', mode='r') as csv_file:
    csv_reader = csv.DictReader(csv_file)

    for row in csv_reader:
        cursor.execute('INSERT INTO `cash_pickup_location` (`branch_name`, `address`, `province`, `type`, `agent`, `payer_id`) VALUES( %s, %s, %s, %s, %s, %s)', (row['Branch Name'], row['Address'], row['Province'], row['Type'], row['Agent'], payer_id))
db.commit()