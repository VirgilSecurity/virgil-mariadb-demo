import styled from "styled-components";

export const Container = styled.div`
  display: flex;
  justify-content: space-between;
  align-items: center;
`;

export const Name = styled.div`
  font-size: 30px;
`;

export const SurName = styled.div`
  font-size: 20px;
`;

export const AddInfoContainer = styled.div`
  display: flex;
  flex-direction: column;
  align-items: center;
`;

export const AddInfo = styled.div`
  font-size: 14px;
  text-align: right;
`;

export const Share = styled.div`
  font-size: 12px;
  cursor: pointer;
  color: link;
`;

export const Label = styled.div`
  font-weight: lighter;
  font-size: 12px;
  color: #000a;
`;

export const PageTitle = styled.h2`
  text-align: center;
`;
export const H3 = styled.h3`
  text-align: center;
`;

export const SectionTitle = styled.div`
  text-align: left;
  color: #000a;
  margin-top: 20px;
  margin-bottom: 5px;
  font-weight: lighter;
`;

export const StyledTableTitle = styled.div`
  color: #000a;
  margin: 15px;
  margin-bottom: 5px;
  font-weight: lighter;
  font-size: 18px;
`;

export const Ellipsis = styled.span`
  width: 160px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  cursor: pointer;
  text-decoration: underline;
  display: block;
`;
