import React, { useState, useEffect } from 'react';
import { GetLabTest, AddResultReq } from './LabEndpoint';
import { TableContainer, Paper, Table, TableHead, TableRow, TableCell, TableBody } from '@material-ui/core';
import { TableTitle } from '../../lib/components/Global';
import { ILabTest } from '../../lib/Interfaces';
import StoreContext from '../StoreContext/StoreContext';
import SimpleModal from '../../lib/components/Modal/Modal';
import AddResult from './AddResult';

interface ItemProps {
    item: ILabTest;
}

const Item: React.FC<ItemProps> = ({ item }) => {
    const { connection } = React.useContext(StoreContext);

    const handleSubmit = (res: string) => {
        connection.send(new AddResultReq({
            ...item,
            results: res
        }, item.id).onSuccess(() => {
            // eslint-disable-next-line no-restricted-globals
            location.reload();
        }));
    };

    return (
        <TableRow>
            <TableCell>{item.id}</TableCell>
            <TableCell>{item.name}</TableCell>
            <TableCell>{item.test_date}</TableCell>
            <TableCell>
                <SimpleModal value="Add">
                    <AddResult onSubmit={handleSubmit}/>
                </SimpleModal>
            </TableCell>
        </TableRow>
    );
};

const Lab = () => {
    const [labTests, setLabsTests] = useState<undefined | ILabTest[]>();
    const { connection } = React.useContext(StoreContext);

    useEffect(() => {
        connection.send(new GetLabTest().onSuccess((resp)=>{
            setLabsTests(resp.filter((el) => !el.results));
        }));
    }, []);

    const renderTable = (data: ILabTest[]) => {
        return (
            <TableContainer component={Paper} style={{margin: '15px 0'}}>
            <TableTitle>Lab tests</TableTitle>
            <Table size="small">
                <TableHead>
                    <TableRow>
                        <TableCell>Test id</TableCell>
                        <TableCell>Name</TableCell>
                        <TableCell>When</TableCell>
                        <TableCell>Results</TableCell>
                    </TableRow>
                </TableHead>
                <TableBody>
                    {data.map((item) => <Item key={item.id} item={item}/>)}
                </TableBody>
            </Table>
        </TableContainer>
        );
    };

    return <>{labTests && renderTable(labTests)}</>;
};

export default Lab;
