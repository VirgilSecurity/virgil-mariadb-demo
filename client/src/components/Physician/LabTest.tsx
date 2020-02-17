import React from 'react';
import { TableContainer, Table, TableHead, TableRow, TableCell, TableBody, makeStyles } from '@material-ui/core';
import { ILabTest } from '../../lib/Interfaces';
import Paper from '@material-ui/core/Paper';
import { TableTitle, TextEllipsis } from '../../lib/components/Global';
import StoreContext from '../StoreContext/StoreContext';
import { ChangePermissionToResultReq } from './PhysicianEndpoint';

export interface LabTestProps {
    data: ILabTest[];
};

interface ItemProps {
    item: ILabTest;
};

const useStyles = makeStyles(() => ({
    btn: {
        color: 'link',
        textAlign: 'right',
        cursor: 'pointer',
    }
}));

const Item: React.FC<ItemProps> = ({ item }) => {
    const css = useStyles();
    const { connection } = React.useContext(StoreContext);

    const handleClick = (id: string, share: boolean) => {
        connection.send(new ChangePermissionToResultReq({
            ...item,
            share: !share,
        }, id).onSuccess(() => {
            // eslint-disable-next-line no-restricted-globals
            location.reload();
        }));
    };
    
    return (
        <TableRow>
            <TableCell>{item.test_name}</TableCell>
            <TableCell>
                {item.results ? 
                <TextEllipsis label={'Result:'}>{item.results}</TextEllipsis>
                :
                <span style={{color: '#e49e24'}}>Not ready</span>}
            </TableCell>
            <TableCell>{item.test_date}</TableCell>
            <TableCell>
                {item.results && !item.share &&
                    <div onClick={() => {handleClick(item.id, item.share)}} className={css.btn}>Share</div>
                }
            </TableCell>
        </TableRow>
    );
};

const LabTest: React.FC<LabTestProps> = ({data}) => {
    return (
        <>
            <TableContainer component={Paper} style={{margin: '15px 0'}}>
                <TableTitle>Lab tests</TableTitle>
                <Table size="small">
                    <TableHead>
                        <TableRow>
                            <TableCell>Name</TableCell>
                            <TableCell>Result</TableCell>
                            <TableCell>When</TableCell>
                            <TableCell>Share to Alice</TableCell>
                        </TableRow>
                    </TableHead>
                    <TableBody>
                        {data.map((item) => <Item key={item.id} item={item}/>)}
                    </TableBody>
                </Table>
            </TableContainer>
        </>
    );
};

export default LabTest;
