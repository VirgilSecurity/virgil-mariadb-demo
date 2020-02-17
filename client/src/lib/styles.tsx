import { makeStyles, Theme, createStyles } from '@material-ui/core/styles';

export const useGlobalStyles = makeStyles((theme: Theme) => 
    createStyles({
    container: {
        display: 'flex',
        justifyContent: 'space-between',
        alignItems: 'center',
    },
    name: {
        fontSize: '30px',
    },
    surName: {
        fontSize: '20px',
    },
    addInfoContainer: {
        display: 'flex',
        flexDirection: 'column',
        alignItems: 'center',
    },
    addInfo: {
        fontSize: '14px',
        textAlign: 'right',
    },
    share: {
        fontSize: '12px',
        cursor: 'pointer',
        color: 'link',
    },
    label: {
        fontWeight: 'lighter',
        fontSize: '12px',
        color: '#000a',
    },
    pageTitle: {
        textAlign: 'center',
    },
    sectionTitle: {
        textAlign: 'left',
        color: '#000a',
        marginTop: '20px',
        marginBottom: '5px',
        fontWeight: 'lighter',
    },
    tableTitle: {
        color: '#000a',
        margin: '15px',
        marginBottom: '5px',
        fontWeight: 'lighter',
        fontSize: '18px',
    },
    ellipsis: {
        width: '160px',
        whiteSpace: 'nowrap',
        overflow: 'hidden',
        textOverflow: 'ellipsis',
        cursor: 'pointer',
        textDecoration: 'underline',
        display: 'block',
    },
}));
