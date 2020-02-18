import React from 'react'
import { TableContainer, Table, TableHead, TableRow, TableCell, TableBody } from '@material-ui/core';
import { IPrescription } from '../../Interfaces';
import Paper from '@material-ui/core/Paper';
import { TableTitle, TextEllipsis } from '../Global';
import { dateCrop } from '../../utils';

export interface PrescriptionsProps {
    data: IPrescription[];
};

interface ItemProps {
    item: IPrescription;
};

const Item:React.FC<ItemProps> = ({item}) => {
    return (
        <TableRow>
            <TableCell >
                <TextEllipsis label={'Notes:'}>{item.notes}</TextEllipsis>
            </TableCell>
            <TableCell >{dateCrop(item.assign_date)}</TableCell>
            <TableCell >{dateCrop(item.release_date)}</TableCell>
        </TableRow>
    );
};

const Prescriptions: React.FC<PrescriptionsProps> = ({data}) => {
    return (
        <>
            <TableContainer component={Paper} style={{margin: '15px 0'}}>
                <TableTitle>Prescriptions</TableTitle>
                <Table size="small">
                    <TableHead>
                        <TableRow>
                            <TableCell>Notes</TableCell>
                            <TableCell>Assign date</TableCell>
                            <TableCell>Release date</TableCell>
                        </TableRow>
                    </TableHead>
                    <TableBody>
                        {data.map((item: IPrescription) => <Item key={item.id} item={item}/>)}
                    </TableBody>
                </Table>
            </TableContainer>
        </>
    );
};

export default Prescriptions;
