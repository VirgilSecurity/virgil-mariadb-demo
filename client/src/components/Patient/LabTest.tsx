import React from 'react';
import { TableContainer, Table, TableHead, TableRow, TableCell, TableBody } from '@material-ui/core';
import { ILabTest } from '../../lib/Interfaces';
import Paper from '@material-ui/core/Paper';
import { TableTitle, TextEllipsis } from '../../lib/components/Global';
export interface LabTestProps {
    data: ILabTest[];
};

interface ItemProps {
    item: ILabTest;
}

const Item: React.FC<ItemProps> = ({ item }) => {
    return (
        <TableRow>
            <TableCell>{item.test_name}</TableCell>
            {item.results ?
                <TableCell>
                    {item.share ?
                        <TextEllipsis label={'Result:'}>{item.results}</TextEllipsis>
                        :
                        <span style={{color: '#fb7267'}}>Access denied</span>
                    }
                </TableCell>
                :
                <TableCell><span style={{color: '#e49e24'}}>Not ready</span></TableCell>
            }
            <TableCell>{item.test_date}</TableCell>
            <TableCell></TableCell>

        </TableRow>
    );
};

const LabTest: React.FC<LabTestProps> = ({data}) => {
    return (
        <TableContainer component={Paper} style={{margin: '15px 0'}}>
            <TableTitle>Lab tests</TableTitle>
            <Table size="small">
                <TableHead>
                    <TableRow>
                        <TableCell>Name</TableCell>
                        <TableCell>Result</TableCell>
                        <TableCell>When</TableCell>
                        <TableCell></TableCell>
                    </TableRow>
                </TableHead>
                <TableBody>
                    {data.map((item) => <Item key={item.id} item={item}/>)}
                </TableBody>
            </Table>
        </TableContainer>
    );
}

export default LabTest;
