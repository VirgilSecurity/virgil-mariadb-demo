import React, { useState, useEffect } from 'react';
import { AddResultReq } from './LabEndpoint';
import { TableContainer, Paper, Table, TableHead, TableRow, TableCell, TableBody } from '@material-ui/core';
import { TableTitle, TextEllipsis } from '../../lib/components/Global';
import { ILabTest, ICredentials, Status } from '../../lib/Interfaces';
import SimpleModal from '../../lib/components/Modal/Modal';
import AddResult from './AddResult';
import { LabTestListReq } from '../../lib/Connection/Endpoints';
import { dateCrop, reloadPage } from '../../lib/utils';
import { Connection } from '../../lib/Connection/Connection';

interface ItemProps {
    item: ILabTest;
    grant: string;
}

const Item: React.FC<ItemProps> = ({ item, grant }) => {
    const connection: Connection = new Connection();

    const handleSubmit = (res: string) => {
        connection.send(new AddResultReq({
            ...item,
            status: 'OK',
            results: res
        }, grant, item.id).onSuccess(reloadPage));
    };

    return (
        <TableRow>
            <TableCell><TextEllipsis>{item.id}</TextEllipsis>{}</TableCell>
            <TableCell>{item.name}</TableCell>
            <TableCell>{dateCrop(item.test_date)}</TableCell>
            <TableCell>
                <SimpleModal value="Add">
                    <AddResult onSubmit={handleSubmit}/>
                </SimpleModal>
            </TableCell>
        </TableRow>
    );
};

export interface LabProps {
    labCred: ICredentials;
};

const Lab:React.FC<LabProps> = ({ labCred }) => {
    const [labTests, setLabTests] = useState<undefined | ILabTest[]>();
    const connection: Connection = new Connection();

    useEffect(() => {
        connection.send(new LabTestListReq(labCred.grant).onSuccess((resp) => {
            setLabTests(resp.filter(it => it.status === Status.notReady));
        }));
    }, [labCred]);

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
                    {data.map((item) => <Item key={item.id} grant={labCred.grant} item={item}/>)}
                </TableBody>
            </Table>
        </TableContainer>
        );
    };

    return <>{labTests && renderTable(labTests)}</>;
};

export default Lab;
