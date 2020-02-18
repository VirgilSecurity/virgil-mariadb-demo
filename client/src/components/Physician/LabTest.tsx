import React from 'react';
import { TableContainer, Table, TableHead, TableRow, TableCell, TableBody, makeStyles } from '@material-ui/core';
import { ILabTest, Status } from '../../lib/Interfaces';
import Paper from '@material-ui/core/Paper';
import { TableTitle, TextEllipsis } from '../../lib/components/Global';
import { dateCrop, reloadPage } from '../../lib/utils';
import { ShareReq } from '../../lib/Connection/Endpoints';
import { Connection } from '../../lib/Connection/Connection';

export interface LabTestProps {
    data: ILabTest[];
    grant: string;
};

interface ItemProps {
    item: ILabTest;
    grant: string;
};

const useStyles = makeStyles(() => ({
    btn: {
        color: 'link',
        textAlign: 'right',
        cursor: 'pointer',
    }
}));

const Item: React.FC<ItemProps> = ({ item, grant }) => {
    const css = useStyles();
    const connection: Connection = new Connection();

    const handleClick = () => {
        connection.send(new ShareReq({
            data_id: item.id,
            share_with: [item.patient_id],
            roles: null
        }, grant).onSuccess(() => {
            localStorage.setItem(item.id, 'true');
            reloadPage();
        }));
    };
    
    return (
        <TableRow>
            <TableCell>{item.name}</TableCell>
            <TableCell>
                {item.results ? 
                <TextEllipsis label={'Result:'}>{item.results}</TextEllipsis>
                :
                <span style={{color: '#e49e24'}}>Not ready</span>}
            </TableCell>
            <TableCell>{dateCrop(item.test_date)}</TableCell>
            <TableCell>
                { item.status === Status.ok && !localStorage.getItem(item.id) && 
                    <div onClick={handleClick} className={css.btn}>Share</div>
                }
            </TableCell>
        </TableRow>
    );
};

const LabTest: React.FC<LabTestProps> = ({data, grant}) => {
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
                        {data.map((item) => <Item grant={grant} key={item.id} item={item}/>)}
                    </TableBody>
                </Table>
            </TableContainer>
        </>
    );
};

export default LabTest;
